#!/usr/bin/python3

import requests 
import sys

# Export OC search index to file 
# O.A 02.12.2019

if len(sys.argv) < 5:
    print("Usage: dump_lucene.py <host> <port> <index> <file>")
    sys.exit()

host = sys.argv[1]
port = sys.argv[2]
index = sys.argv[3]
fileName = sys.argv[4]

hitsPerPage = 50

url_index = 'http://{}:{}/index/{}?query=*:*&start={}&hitsPerPage={}'
url_doc = 'http://{}:{}/index/{}/{}'

req = requests.get(url_index.format(host,port,index,0,hitsPerPage))
pages = (int)(req.json()['totalHits']/hitsPerPage)
page = 0
with open(fileName,"w") as file: 
    file.write("[\n")
    i =  0
    while page <= pages:                
        start=page*hitsPerPage    
        r = requests.get(url_index.format(host,port,index,start,hitsPerPage))
        for d in r.json()['hits']:            
            id = d['id']
            path = d['path']
            sys.stdout.write('#')
            r = requests.get(url_doc.format(host,port,index,id))
            if (page != 0 or i != 0):
                file.write(",")    
            file.write(r.text)
            i = i + 1                        
        page = page + 1
    file.write("]\n")
    print("\nExport finished.")