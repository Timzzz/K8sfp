#!/bin/sh

# Modified from https://github.com/hakobera/docker-locust

LOCUST_CMD="/usr/local/bin/locust"
LOCUST_OPTS="-f $SCENARIO_FILE --host=$TARGET_URL"
LOCUST_MODE=${LOCUST_MODE:-standalone}

if [ "$LOCUST_MODE" = "master" ]; then
  LOCUST_OPTS="$LOCUST_OPTS --master"
elif [ "$LOCUST_MODE" = "slave" ]; then
  LOCUST_OPTS="$LOCUST_OPTS --slave --master-host=$MASTER_HOST"
fi

if [ -z $LOCUST_FILE_URL ]; then
	>&2 echo 'Error: $LOCUST_FILE_URL not set'
	exit
fi
echo "Downloading locustfile"
echo $LOCUST_FILE_URL
wget -O /locustfile.py $LOCUST_FILE_URL

INFLUXDB_URL=${INFLUXDB_URL:-"http://influxdb"}
INFLUXDB_PORT=${INFLUXDB_PORT:-"8086"}
echo "INFLUXDB_URL=$INFLUXDB_URL"
echo "INFLUXDB_PORT=$INFLUXDB_PORT"

echo "=> Starting locust"
echo "$LOCUST_CMD $LOCUST_OPTS"

$LOCUST_CMD $LOCUST_OPTS
