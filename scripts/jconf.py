
import csv
import json

json_data = open("pull.json")
data = json.load(json_data)

f = csv.writer(open("fix_hists.csv","wb+"))

#for i in data['results'][0]['series'][0]['columns']:
f.writerow(data['results'][0]['series'][0]['columns'])

for i in data['results'][0]['series'][0]['values']:
	f.writerow(i)

