package de.unibayreuth.bayceer.oc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.unibayreuth.bayceer.oc.entity.ReadmeDocument;
import de.unibayreuth.bayceer.oc.entity.TermCount;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FieldController {
	@Autowired
	RestHighLevelClient client;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/field/{collection}/names")
	public List<String> fields(@PathVariable String collection) throws IOException {
		log.debug("Get field names of {}", collection);
		List<String> ret = new ArrayList<String>();
		GetMappingsRequest req = new GetMappingsRequest();
		req.indices(collection);
		GetMappingsResponse resp = client.indices().getMapping(req, RequestOptions.DEFAULT);				 
		MappingMetaData indexMapping = resp.mappings().get(collection); 				
		@SuppressWarnings("unchecked")		
		Map<String,Object> fields = (Map<String, Object>) indexMapping.sourceAsMap().get("properties");		
		fields.forEach((key,value) -> {
			if (!key.startsWith(ReadmeDocument.SYSTEM_FIELD_PREFIX)) {
				ret.add(key);	
			}			
		});
		return ret;
	}

	@GetMapping("/field/{collection}/cardinality")
	public List<TermCount> cardinality(@PathVariable String collection) throws IOException {
		log.debug("Get field cardinality of {}", collection);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		fields(collection).forEach(f -> {
			searchSourceBuilder.aggregation(AggregationBuilders.cardinality(f).field(f + ".keyword"));	
		});
		  		
		SearchRequest searchRequest = new SearchRequest(collection);
		searchRequest.source(searchSourceBuilder);		
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		
		List<TermCount> ret = new ArrayList<TermCount>();	
		for(Aggregation a:searchResponse.getAggregations()) {		
			ParsedCardinality ps = (ParsedCardinality)a;			
			ret.add(new TermCount(ps.getName(),ps.getValue()));						
		}		
		return ret;
	}
}
