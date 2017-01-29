#!bin/bash

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

bash pull.sh $limit_count $container_pattern
python createCsv.py
java -jar CsvToArffConverter.jar createCsv.csv

