#!/usr/bin/python3
import requests 

index = "owncloud"
hitsPerPage = 50
lport = 5540
eport = 5541 

url_index = 'http://localhost:{}/index/{}?query=*:*&start={}&hitsPerPage={}'
url_doc = 'http://localhost:{}/index/{}/{}'
url_post = 'http://localhost:{}/{}/{}'

req = requests.get(url_index.format(lport,index,0,hitsPerPage))
pages = (int)(req.json()['totalHits']/hitsPerPage)
page = 0
while page <= pages:    
    start=page*hitsPerPage
    print('Page:{} Start:{}'.format(page,start))
    r = requests.get(url_index.format(lport,index,start,hitsPerPage))
    for d in r.json()['hits']:
        id = d['id']
        path = d['path']
        r = requests.get(url_doc.format(lport,index,id))
        doc = r.json()
        content = doc['content']
        lastModified = doc['lastModified']
        print('key:{} path:{} lastModified:{}'.format(id,path,lastModified))        
        payload = {'key':id, 'path': path, 'content': content, 'lastModified': lastModified}
        pr = requests.post(url_post.format(eport,index,id), json=payload)        
    page = page + 1