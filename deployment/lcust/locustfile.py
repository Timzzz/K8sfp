from locust import HttpLocust, TaskSet, task
from locust import events
from random import randint
import json
import requests
from influxdb import InfluxDBClient
import os
from requests.exceptions import ConnectionError
import urllib2
import schedule
import time

class MyTaskSet(TaskSet):
    
	def scheduled_write(self):
		json_body = [
		{
			"measurement": "test_results",
			"tags": {
			"curr_requests": curr_requests,
			"curr_fails": curr_fails
		},
			"fields": {
       			"curr_requests": curr_requests,
			"curr_fails": curr_fails
			}
		}
		]
		InfluxDBWriter.write(json_body)

    def on_start(self):
        self.user_id = randint(1,999999999)
	schedule.every(1).seconds.do(scheduled_write)

    @task(1)
    def view(self):
        with self.client.get("/jsp/rss.jsp", catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addAbc(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/abc.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addBbc(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/bbc.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addCnn(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/cnn.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addDw(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/dw.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addForbes(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/forbes.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addReuters(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/reuters.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addSpiegel(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/spiegel.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def addWsj(self):
        with self.client.post("/jsp/rss.jsp", {"url":"http://rssserver/wsj.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteAbc(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/abc.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteBbc(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/bbc.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteCnn(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/cnn.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteDw(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/dw.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteForbes(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/forbes.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteReuters(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/reuters.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteSpiegel(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/spiegel.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    @task(1)
    def deleteWsj(self):
        with self.client.post("/jsp/rss.jsp", {"delFeedUrl":"http://rssserver/wsj.xml", "username":self.user_id}, catch_response=True) as response:
            self.log_response(response)

    def log_response(self, response):
	global curr_requests
	global curr_fails
	try:
	    if curr_requests is None:
		curr_requests = 0
    		curr_fails = 0
	except NameError:
		curr_requests = 0
		curr_fails = 0
	
	curr_requests = curr_requests + 1
	if(response.status_code != 200):
		curr_fails = curr_fails + 1
	schedule.run_pending()
        

class MyLocust(HttpLocust):
    task_set = MyTaskSet
    min_wait = 5000
    max_wait = 10000

class InfluxDBWriter():
    connected = False
    client = None

    @staticmethod
    def connect():
        influxdb_url = os.environ['INFLUXDB_URL']
        influxdb_port = os.environ['INFLUXDB_PORT']
        print("Connecting to InfluxDB")
        InfluxDBWriter.client = InfluxDBClient(influxdb_url, influxdb_port, 'root', 'root', 'locust')
        InfluxDBWriter.client.create_database('locust')
        InfluxDBWriter.connected = True
        print("Connected to InfluxDB")

    @staticmethod
    def write(json_body):
        try:
            if (InfluxDBWriter.connected == False):
                InfluxDBWriter.connect()
            InfluxDBWriter.client.write_points(json_body)
        except ConnectionError as e:
            InfluxDBWriter.connected = False
            print("ERROR: Cannot connect to InfluxDB. Dropping data point. See exception below for details.")
            print(e)
        except urllib2.HTTPError as e:
            InfluxDBWriter.connected = False
            print("ERROR: Database locust does not exist. Dropping data point. Will attempt to reconnect and create database.")
            print(e)

def log_user_count(user_count, **kw):
    json_body = [
            {
                "measurement": "user_count",
                "fields": {
                    "user_count": user_count
                    }
                }
            ]
    print(json_body)
    InfluxDBWriter.write(json_body)

def log_stop(**kw):
    log_user_count(0)

events.hatch_complete += log_user_count
events.locust_stop_hatching += log_stop