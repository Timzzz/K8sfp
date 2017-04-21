#!/usr/bin/env python2

import time
import json
import requests
import random
#from influxdb import InfluxDBClient

# IP of one of the k8s nodes
k8s_node_ip = "10.0.11.61" #127.0.0.1"
locust_master_port = "31050"

# (num_users, duration in minutes)
profile = [(3,3),(5,3),(10,3),(20,3),(50,3),(100,3),(150,3),(200,3),(250,3),(300,3),(350,3),(400,3),(500,3),(600,3)]

def create_profile(count, maxUsers):
    intervals = []
    max_interval = 0
    for p in range(count):
        max_interval += random.randint(0, int(maxUsers / int(count/2)))
        intervals.append(new_interval)
    print("profile: ", intervals)
    
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
#change_workload(0)
create_profile(14, 600)