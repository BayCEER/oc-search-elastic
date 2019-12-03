#!/bin/bash
curl -H "Content-Type: application/x-ndjson" -XPOST "localhost:9200/default/_bulk" --data-binary "@default.json"
echo "\n"
