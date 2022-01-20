#!/bin/bash
 curl 'http://localhost:5541/owncloud/indexes' -i -X POST \
    -H 'Accept: application/json' \
    -H 'Content-Type: application/json; charset=UTF-8' \
    --data-binary "@owncloud.json"
