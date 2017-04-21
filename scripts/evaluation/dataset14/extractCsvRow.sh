#!/bin/bash

display_usage() { 
	echo -e "\nUsage: <Program> <Row to extract> <File>" 
}

if [  $# -le 0 ]; then 
	display_usage
	exit 1
fi 

awk -F "\"*\t\"*" "{print \$$1}" $2 > "$2.extract.csv"
