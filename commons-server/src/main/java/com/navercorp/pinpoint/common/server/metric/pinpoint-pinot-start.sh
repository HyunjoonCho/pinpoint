#!/bin/bash

ZK_HOST=localhost
ZK_PORT=2191

while true; do
  [ $# -eq 0 ] && break
  case $1 in
    --host)
    shift
    ZK_HOST=$1
    shift; continue
    ;;
    --port)
    shift
    ZK_PORT=$1
    shift; continue
    ;;
    --*)
    echo "Invalid option $1"
    exit 1
    ;;
  esac
done

echo "zookeeper host: $ZK_HOST"
echo "zookeeper port: $ZK_PORT"

apache-pinot-incubating-0.6.0-bin/bin/pinot-admin.sh StartZookeeper -zkPort $ZK_PORT >& /dev/null &
echo "started pinot-zookeeper"
apache-pinot-incubating-0.6.0-bin/bin/pinot-admin.sh StartServer -zkAddress $ZK_HOST:$ZK_PORT >& /dev/null &
echo "started pinot-server"
apache-pinot-incubating-0.6.0-bin/bin/pinot-admin.sh StartBroker -zkAddress $ZK_HOST:$ZK_PORT &
echo "started pinot-broker"
apache-pinot-incubating-0.6.0-bin/bin/pinot-admin.sh StartController -zkAddress $ZK_HOST:$ZK_PORT -controllerPort 9000 >& /dev/null &
echo "started pinot-controller"
kafka_2.13-2.6.0/bin/kafka-server-start.sh kafka_2.13-2.6.0/config/server.properties &
echo "started kafka-server"