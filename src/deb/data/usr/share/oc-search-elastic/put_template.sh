#/!bin/bash
curl -XPUT "localhost:9200/_template/readme" -H 'Content-Type: application/json' -d'
{
  "index_patterns":["oc*","owncloud*"],
   "settings": {},
   "mappings": {
        "properties": {
            "_content": {
                "type": "text",
                "index":"false"                
            }
        }
    }
}
'
echo