#!/bin/bash

function analyze {
        mkdir -p data
        filen=$1
        rowExtract=$2
        sh extractCsvRow.sh $rowExtract $filen
        filen=$filen".extract.csv"
        mv $filen data  
        filen="data/"$filen                                                             # adjust filename
        tail -n +2 "$filen" > temp && mv temp $filen    # remove header
        tac $filen > temp && mv temp $filen                             # reverse
        Rscript holtwinters.r $filen                                    # analyze file
}

run_number=$1
name="_RUN"$run_number

mkdir results

echo "### Dropping DB k8s..."
#curl -XPOST 'http://172.16.38.2:8086/query' --data-urlencode "q=drop database k8s"
curl http://172.16.38.2:8086/query?q=DROP+DATABASE+"k8s"

echo "### Dropping DB locust..."
#curl http://172.16.22.4:8086/query?q=DROP+DATABASE+"locust"
#curl http://172.16.22.4:8086/query?q=CREATE+DATABASE+"locust"
curl -XPOST 'http://172.16.22.4:8086/query' --data-urlencode "q=drop database locust"
curl -XPOST 'http://172.16.22.4:8086/query' --data-urlencode "q=create database locust"

echo "### restarting locust..."
sh kubeRemovePods.sh locust-master
sleep 10
sh kubeRemovePods.sh locust-worker
sleep 10

echo "### waiting for timeout"
sleep 60

echo "### running locust simulation..."
python run-locust2.py > run-locust_RUN1.log

echo "### collecting data..."
java -jar K8sfpEvaluator.jar "eventlog"$name".csv"

echo "### extracting data..."
sh extractCsvRow.sh 2 "eventlog"$name".csv"

echo "### forcasting data..."
analyze "eventlog_RUN1.csv" 2

echo "### moving data..."
mv *.csv data
mv *.log data
mv data results/data$name

echo "### Done!"