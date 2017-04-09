#!/usr/bin/env python2

import time
import json
import requests
from subprocess import call

# (num_users, duration in minutes)
profile = [(3,3),(5,3),(10,3),(20,3),(50,3),(100,3),(150,3),(200,3),(250,3),(300,3),(350,3),(400,3),(500,3),(600,3)]

for p in profile:
    print(time.strftime("%Y-%m-%d %H:%M:%S"), int(time.time()), "workers=", p[0], "Duration=", p[1]*60, "min")
    call(["stress", "--cpu", p[1]*60, "--timeout", p[0]])
    

# Stop at the end
change_workload(0)
