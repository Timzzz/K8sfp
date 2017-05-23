#!/bin/bash

display_usage() { 
	echo -e "\nUsage: <Program> <Row to extract> <FILENAME>" 
}

if [  $# -le 0 ]; then 
	display_usage
	exit 1
fi 

echo $2
awk -F "\"*[\t,]\"*" "{print \$$1}" $2
