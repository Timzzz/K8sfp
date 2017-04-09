
# get kieker logs
bash pull.sh 100 "" kiekerlogs mydb kiekerlogs.json "host =~ /edge/"

# get CPU logs
bash pull.sh 100 "edge" "cpu/usage" k8s cpuusage.json

