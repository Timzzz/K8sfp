#!/usr/bin/env python2

import time
import json
import requests
from influxdb import InfluxDBClient

# IP of one of the k8s nodes
k8s_node_ip = "127.0.0.1"
locust_master_port = "31050"

# (num_users, duration in minutes)
profile = [(0,5),(5,5),(10,5),(20,5),(50,5),(100,5),(150,5),(200,5),(250,5),(300,5),(350,5),(400,5),]

def change_workload(num_users):
    if (num_users <= 0):
        r = requests.get("http://" + k8s_node_ip + ":" + locust_master_port + "/stop")
    else:
        payload = {'locust_count': num_users, 'hatch_rate': num_users}
        r = requests.post("http://" + k8s_node_ip + ":" + locust_master_port + "/swarm", data=payload)

for p in profile:
    print(time.strftime("%Y-%m-%d %H:%M:%S"), int(time.time()), "Users=", p[0], "Duration=", p[1], "min")
    change_workload(p[0])
    time.sleep(p[1]*60)

# Stop at the end
change_workload(0)

