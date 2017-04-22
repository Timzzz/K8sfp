import csv
import itertools as IT
import sys

args=(sys.argv)
filenames = [args[1], args[2]]
print filenames
handles = [open(filename, 'rb') for filename in filenames]    
readers = [csv.reader(f, delimiter='\t') for f in handles]

with  open('combined.csv', 'wb') as h:
    writer = csv.writer(h, delimiter='\t', lineterminator='\n', )
    for rows in IT.izip_longest(*readers, fillvalue=['']*2):
        combined_row = []
        for row in rows:
            row = row # select the columns you want
            if len(row) >= 1:
                combined_row.extend(row)
            else:
                combined_row.extend(['']*2)
        writer.writerow(combined_row)

for f in handles:
    f.close()

