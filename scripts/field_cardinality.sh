#!/bin/bash
curl -X POST "localhost:9200/default/_search?pretty"  -H "Content-Type:application/json" -d'
{
   "size": 0,
    "aggs" : {
      "creators" : { "cardinality" : { "field" : "creator.keyword" } },
      "titles" : { "cardinality" : { "field" : "title.keyword" } }
    }
}
'