#!/usr/bin/python
import csv
import sys

args=(sys.argv)

print 'Number of arguments:', len(sys.argv), 'arguments.'
print 'Argument List:', str(sys.argv)

data = []

with open(args[1], "rb") as fp:
     reader = csv.reader(fp, delimiter='\t', quotechar='|')
     for row in reader:
     	idx = row.index(args[2])
     	print idx
     	break;
     data.append(args[2])
     for row in reader: # Iterate the remaining rows
    	data.append(row[idx])

print(data)

with open(args[3], "wb") as wr:
	for item in data:
  		wr.write("%s\n" % item)

