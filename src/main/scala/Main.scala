package com.dennybritz.akkadeploy

import scala.io.Source
import com.typesafe.config.ConfigFactory
import akka.actor._
import akka.cluster.Cluster
import scala.util.{Try, Success, Failure}
import java.net.InetAddress

object Main extends App {

  lazy val log = org.slf4j.LoggerFactory.getLogger(this.getClass.getName)

  // Load the configuration
  // Refer to https://github.com/typesafehub/config for more information
  val config = ConfigFactory.load()
  // Start the Akka actor system 
  val system = ActorSystem("app", config)

  // Read cluster seed nodes from the file specified in the configuration
  val seeds = Try(config.getString("app.cluster.seedsFile")).toOption match {
    case Some(seedsFile) =>
      // Seed file was specified, read it
      log.info(s"reading seed nodes from file: ${seedsFile}")
      Source.fromFile(seedsFile).getLines.map { address =>
        AddressFromURIString.parse(s"akka.tcp://app@${address}")
      }.toList
    case None => 
      // No seed file specified, use this node as the first seed
      log.info("no seed file found, using default seeds")
      val port = config.getInt("app.port")
      val localAddress = Try(config.getString("app.host"))
        .toOption.getOrElse(InetAddress.getLocalHost.getHostAddress.toString)
      List(AddressFromURIString.parse(s"akka.tcp://app@${localAddress}:${port}"))
  }

  // Join the cluster with the specified seed nodes and block until termination
  log.info(s"Joining cluster with seed nodes: ${seeds}")
  Cluster.get(system).joinSeedNodes(seeds.toSeq)
  system.awaitTermination()

}