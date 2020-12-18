#!/bin/bash

kafka_2.13-2.6.0/bin/kafka-server-stop.sh
process_id=$!
wait $process_id
echo "stopped kafka-server"

pid=$(pgrep -f PinotAdministrator\ StartController)
kill $pid
echo "stopped pinot-controller"
pid=$(pgrep -f PinotAdministrator\ StartBroker)
kill $pid
echo "stopped pinot-broker"
pid=$(pgrep -f PinotAdministrator\ StartServer)
kill $pid
echo "stopped pinot-server"
pid=$(pgrep -f PinotAdministrator\ StartZookeeper)
kill $pid
echo "stopped pinot-zookeeper"
#apache-pinot-incubating-0.6.0-bin/bin/pinot-admin.sh StopProcess -controller -server -broker