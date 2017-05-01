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

def create_linear_profile(count, maxUsers, execTime):
    intervals = [(1,1)]
    max_interval = 0
    for i in range(count):
        max_interval += random.randint(1, int(maxUsers / int(count/(2))))
        intervals.append((max_interval,execTime))
    if(max_interval > maxUsers):
    	for i in range(count+1):
            new_val = max(0, intervals[i][0] - int((max_interval-maxUsers)/((count+1)/(i+1))))
            intervals[i] = (new_val, execTime)
    if(intervals[count][0] < maxUsers):	# reroll
        intervals = create_linear_profile(count, maxUsers, execTime)
    else:
        print("profile: ", intervals)
    return intervals
        
def change_workload(num_users):
    if (num_users <= 0):
        r = requests.get("http://" + k8s_node_ip + ":" + locust_master_port + "/stop")
    else:
        payload = {'locust_count': num_users, 'hatch_rate': num_users}
        r = requests.post("http://" + k8s_node_ip + ":" + locust_master_port + "/swarm", data=payload)

intervals = create_linear_profile(15, 600, 3)

for p in profile:
    print(time.strftime("%Y-%m-%d %H:%M:%S"), int(time.time()), "Users=", p[0], "Duration=", p[1], "min")
    change_workload(p[0])
    time.sleep(p[1]*60)

# Stop at the end
change_workload(0)

