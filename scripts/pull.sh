#!/bin/bash

display_usage() { 
	echo -e "\nUsage: <Program> <LIMIT> [CONTANER_NAME_PATTERN]" 
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

STR_SEL="q=SELECT value, container_name "
STR_FROM="FROM \"cpu/usage_rate\" "
STR_WHERE="WHERE namespace_name =~ /.*default/ AND container_name =~ /.+/ "
STR_LIMIT="LIMIT $limit_count "

if [ ! -z $container_pattern ]; then
	STR_CNAME="AND container_name =~ /$container_pattern/ "
else
	STR_CNAME=""
fi

STR_QUERY=$STR_SEL$STR_FROM$STR_WHERE$STR_CNAME$STR_LIMIT
echo $STR_QUERY

curl -G --proxy timzwietasch:"n2(rSR6oi@192".168.209.235:8888 \
	"http://10.0.6.56:31843/query?pretty=true" \
	--data-urlencode "db=k8s" \
	--data-urlencode "$STR_QUERY" \
	> pull.json

#STR_ORIG="q=SELECT value, container_name  FROM \"cpu/usage_rate\" WHERE namespace_name =~ /.*default/ AND container_name =~ /.+/ LIMIT $limit_count" 

