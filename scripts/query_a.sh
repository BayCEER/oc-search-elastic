#!/bin/bash
curl -X GET "localhost:9200/default/_search?pretty" -H 'Content-Type: application/json' -d'
{
    "from" : 0, "size" : 100,
    "query": {
        "bool": {
            "must": {
                "simple_query_string": {
                    "query": "Maggie",            
                    "fields": ["_content.search"]
                }
            },
            "filter": [{"term" : {"creator.keyword":"Maggie Simpson"}}]
        }
    },                                           
    "highlight" : {
        "fields" : {            
            "_content.search" : {  "pre_tags" : ["<mark>"], "post_tags" : ["</mark>"] }
        }
    },
    "aggs": {
        "creators": {
            "terms": {
                "field": "creator.keyword"
            }
        }
    }
}
'
