package de.unibayreuth.bayceer.oc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.unibayreuth.bayceer.oc.entity.ReadmeDocument;

@RestController
@SuppressWarnings("unchecked")
public class FieldController {
	
	@Autowired
	RestHighLevelClient client;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@GetMapping(value="/{collection}/field/names",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<String> names(@PathVariable String collection, @RequestParam(defaultValue = "false") Boolean sysFields) throws IOException {
		log.debug("Get field names of {}", collection);
		List<String> fields = new ArrayList<String>();
						
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();		
		properties(collection).forEach((key,value) -> {
			if ( !(key.startsWith(ReadmeDocument.SYSTEM_FIELD_PREFIX)&&(!sysFields))) {
				searchSourceBuilder.aggregation(AggregationBuilders.count(key).field(key + ".keyword"));
			}			
		});
		
		SearchRequest searchRequest = new SearchRequest(collection);
		searchRequest.source(searchSourceBuilder);		
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		
		// Filter out all fields with no docs
		for(Aggregation a:searchResponse.getAggregations()) {			
			ParsedValueCount  ps = (ParsedValueCount)a;
			if (ps.getValue()>0) {
				fields.add(ps.getName());
			}										
		}		
		return fields;
	}
	
			
	
	public Map<String,Object> properties(String collection) throws IOException{				
		if (client.indices().exists(new GetIndexRequest(collection),RequestOptions.DEFAULT)) {
			GetMappingsRequest req = new GetMappingsRequest();
			req.indices(collection);
			GetMappingsResponse resp = client.indices().getMapping(req, RequestOptions.DEFAULT);				 
			MappingMetaData indexMapping = resp.mappings().get(collection); 								
			return (Map<String, Object>) indexMapping.sourceAsMap().get("properties");
		} else {
			  return new HashMap<String, Object>();
		}
	}
		
	public Map<String,String> types(String collection) throws IOException {	
		Map<String,String> ret = new HashMap<String, String>();
		properties(collection).forEach((k,v) -> {						
				Map<String,Object> prop = (Map<String, Object>) v;
				ret.put(k,prop.get("type").toString());			
		});
		return ret;
	}
				
}
