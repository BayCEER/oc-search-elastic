package de.unibayreuth.bayceer.oc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.unibayreuth.bayceer.oc.entity.ReadmeDocument;

@RestController
public class FieldController {
	@Autowired
	RestHighLevelClient client;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@GetMapping(value="/{collection}/field/names",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
	
}
