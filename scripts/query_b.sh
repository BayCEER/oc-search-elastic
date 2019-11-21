#!/bin/bash
curl -X GET "localhost:9200/default/_search?pretty" -H 'Content-Type: application/json' -d'
{
    "from" : 0, "size" : 100,
    "query": {
        "bool": {
            "must": {
                "simple_query_string": {
                    "query": "microplastics",
                    "fields": ["_content.search"]
                }
            },
            "filter": [
                {"terms" : {"creator.keyword":["Maggie Simpson","Bart Simpson"]}},
                {"terms" : {"publisher.keyword":["University of Landshut"]}}
            ]
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
