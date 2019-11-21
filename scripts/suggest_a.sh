#!/bin/bash
curl -X GET "localhost:9200/default/_suggest?pretty" -H 'Content-Type: application/json' -d'
{
    "name-autocomplete": {
        "text":"Ma",
        "completion": {
            "field": "content.suggest"
        }
    }
}
'
