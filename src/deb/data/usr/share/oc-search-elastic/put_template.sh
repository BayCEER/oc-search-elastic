#/!bin/bash
curl -XPUT "localhost:9200/_template/readme" -H 'Content-Type: application/json' -d'
{
  "index_patterns":["oc*","owncloud*"],
   "settings": {    
        "analysis": {      
            "analyzer": {        
                "readme_analyzer": {                	         
                    "tokenizer": "standard",          
                    "char_filter": ["colon_to_space"],
                    "filter": ["lowercase"]        
                }             
            },      
            "char_filter": {
                "colon_to_space": {
                    "type": "mapping",          
                    "mappings": [            
                        ": => \"\""          
                    ]        
                }
            }
                
        }      
    },
    "mappings": {
        "properties": {
            "_content": {
                "type": "text",
                "fields": {
                    "search": {
                        "type": "text",
                        "analyzer": "readme_analyzer"
                    }                    
                }
            }
        }
    }

}
'
echo