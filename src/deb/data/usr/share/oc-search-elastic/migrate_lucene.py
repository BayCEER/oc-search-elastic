#!/usr/bin/python3
import requests 
import sys
import os

# Migrate OC Search Lucene to OC Search Elastic 
# O. A. 12.12.2019

if len(sys.argv) < 3:
    print("Usage: migrate_lucene.py <lucene index> <elastic index>")
    sys.exit()

lu_name =  sys.argv[1]
el_name =  sys.argv[2]
hitsPerPage = 50
lu_port = 5540
lu_index = 'http://localhost:{}/index/{}?query=*:*&start={}&hitsPerPage={}'
lu_doc = 'http://localhost:{}/index/{}/{}'
el_port = 5541 
el_index = 'http://localhost:{}/{}/index/{}'

req = requests.get(lu_index.format(lu_port,lu_name,0,hitsPerPage))
pages = (int)(req.json()['totalHits']/hitsPerPage)
page = 0
while page <= pages:    
    start=page*hitsPerPage    
    r = requests.get(lu_index.format(lu_port,lu_name,start,hitsPerPage))
    for d in r.json()['hits']:
        id = d['id']
        path = d['path']
        r = requests.get(lu_doc.format(lu_port,lu_name,id))
        doc = r.json()
        content = doc['content']
        lastModified = doc['lastModified']        
        payload = {'key':id, 'path': path, 'content': content, 'lastModified': lastModified}
        sys.stdout.write('#')
        pr = requests.post(el_index.format(el_port,el_name,id), json=payload)        
    page = page + 1
 
print("\nFinished.")