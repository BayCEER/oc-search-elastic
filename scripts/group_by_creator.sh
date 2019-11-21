#!/bin/bash
curl -X GET "localhost:9200/default/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "size": 0,
  "aggs": {
    "creators": {
      "terms": {
        "field": "creator.keyword"
      }
    }
  }
}
'
