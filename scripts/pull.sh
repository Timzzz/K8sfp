#!/bin/bash

display_usage() { 
	echo -e "\nUsage: <Program> <LIMIT> [CONTANER_NAME_PATTERN] [MEASURE_NAME] [TABLE_NAME] [OUTPUT_FILE] [WHERE-CLAUSE]" 
}

if [  $# -le 0 ]; then 
	display_usage
	exit 1
fi 
if [[ $# == "--help" || $# == "-h" ]]; then 
	display_usage
	exit 0
fi

limit_count=$1
container_pattern=$2
measure=$3
tablename=$4
outputfile=$5
whereclause=$6

if [ ! -z $container_pattern ]; then
	STR_CNAME="AND container_name =~ /$container_pattern/ "
else
	STR_CNAME=""
fi
if [  -z $measure ]; then
	measure="cpu/usage_rate"
fi
if [  -z $tablename ]; then
	tablename="k8s"
fi
if [  -z $outputfile ]; then
	outputfile="pull.json"
fi
if [  -z $whereclause ]; then
	whereclause="container_name =~ /.+/"
fi
STR_SEL="q=SELECT * "
STR_FROM="FROM \"$measure\" "
STR_WHERE="WHERE $whereclause "
STR_LIMIT="ORDER BY DESC LIMIT $limit_count"

STR_QUERY=$STR_SEL$STR_FROM$STR_WHERE$STR_CNAME$STR_LIMIT
echo "$STR_QUERY"

curl -G --proxy timzwietasch:"n2(rSR6oi"@192.168.209.235:8888 \
	"http://10.0.11.61:31070/query?pretty=true" \
	--data-urlencode "db=$tablename" \
	--data-urlencode "$STR_QUERY" \
	> $outputfile


