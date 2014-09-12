FROM centos:centos7
MAINTAINER Denny Britz <dennybritz@gmail.com>

# Install packages and SBT
RUN yum update
RUN yum -y groupinstall "Development Tools"
RUN yum -y install java java-devel wget
RUN wget http://dl.bintray.com/sbt/rpm/sbt-0.13.5.rpm; rpm -i sbt-0.13.5.rpm;

# Pull code from github
RUN mkdir -p /root/.ssh/
RUN echo -e "Host github.com\n\tStrictHostKeyChecking no\n" >> /root/.ssh/config
RUN git clone https://github.com/dennybritz/akka-cluster-deploy /app

# Compile the Scala code
WORKDIR /app
RUN sbt compile