# Clone the github repo (for the service files)
git clone https://github.com/dennybritz/akka-cluster-deploy akka-cluster-deploy 

# Submit the unit files to fleet
fleetctl submit akka-cluster-deploy/deploy/coreos/app@.service
fleetctl submit akka-cluster-deploy/deploy/coreos/app-discovery@.service

# Schedule a unit (with a specific port) onto a server
# (This doesn't start the unit yet)
fleetctl load app@5000.service
fleetctl load app-discovery@5000.service

# Start the units (app-discovery is automatically started through the requirement)
fleetctl start app@5000.service

# Tail the application output to make sure it's running correctly
# Quit with ctrl+c
fleetctl journal -f app@5000.service

# Load and schedule another unit
fleetctl load app@5001.service app-discovery@5001.service
fleetctl start app@5001.service

# List units
fleetctl list-units

# Tail the first application to make sure the second has joined the cluster
fleetctl journal -f app@5000.service