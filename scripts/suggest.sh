#!/bin/bash
curl -X POST "localhost:9200/default/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "suggest": {
    "text" : "Bar"        
  }
}
'
