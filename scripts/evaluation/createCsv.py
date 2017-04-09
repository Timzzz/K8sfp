
import csv
import json
from datetime import datetime

json_data = open("pull.json")
data = json.load(json_data)

#f = csv.writer(open("fix_hists.csv","wb+"))
f = csv.writer(open("createCsv.csv","wb+"), delimiter=',',
                lineterminator='\r\n',
                quotechar = "'"
                )

#for i in data['results'][0]['series'][0]['columns']:
f.writerow(data['results'][0]['series'][0]['columns'])

for i in data['results'][0]['series'][0]['values']:
	d = datetime.strptime(i[0], '%Y-%m-%dT%H:%M:%SZ')
	i[0] = (d.strftime("\"%Y-%m-%d %H:%M:%S\""))
	#print i[0]	
	f.writerow(i)

