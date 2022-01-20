package de.unibayreuth.bayceer.oc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import de.unibayreuth.bayceer.oc.NoSuchDocException;
import de.unibayreuth.bayceer.oc.entity.ReadmeDocument;
import de.unibayreuth.bayceer.oc.entity.ReadmeMapAdapter;
import de.unibayreuth.bayceer.oc.parser.ReadmeParserException;

@RestController
public class DocumentController {

	@Autowired
	RestHighLevelClient client;
	
	@Autowired
	FieldController fieldController;

	private final Logger log = LoggerFactory.getLogger(this.getClass());	
		

	@PostMapping(value = "/{collection}/index/{key}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void create(@PathVariable String collection, @PathVariable String key, @RequestBody ReadmeDocument doc)
			throws IOException, ReadmeParserException {
		
		log.debug("Index file:{} in collection:{} with Key:{}", doc.getPath(),collection,key);	
		Map<String,Object> s = ReadmeMapAdapter.toMap(doc,fieldController.types(collection));
		log.debug("Document parsed");
		IndexRequest req = new IndexRequest(collection);
		req.id(key);								
		req.source(s);				
		IndexResponse res = client.index(req, RequestOptions.DEFAULT);
		Result result = res.getResult();
		log.debug("Document indexed");
		log.debug("Doc:{} Result:{}", key, result.getLowercase());	
		
		
	}
	
	@GetMapping(value="/{collection}/index/{key}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ReadmeDocument read(@PathVariable String collection, @PathVariable String key)
			throws IOException, NoSuchDocException {
		log.debug("Get doc:{} collection:{}", key, collection);
		GetRequest req = new GetRequest(collection, key);
		GetResponse res = client.get(req, RequestOptions.DEFAULT);
		if (res != null && res.isExists()) {											
			return ReadmeMapAdapter.fromMap(res.getSourceAsMap());			
		} else {
			log.warn("Doc:{} not found.", key);
			throw new NoSuchDocException(key);
		}
	}
	
		
				
	// Replace
	@PutMapping(value="/{collection}/index/{key}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void update(@PathVariable String collection, @PathVariable String key, @RequestBody ReadmeDocument doc) throws IOException, ReadmeParserException {		
		create(collection, key, doc);		
	}

	
	@DeleteMapping(value="/{collection}/index/{key}")
	public void delete(@PathVariable String collection, @PathVariable String key) throws IOException {
		log.debug("Delete doc:{} collection:{}", key, collection);
		DeleteRequest req = new DeleteRequest(collection, key);
		DeleteResponse res = client.delete(req, RequestOptions.DEFAULT);
		log.debug("Doc:{} Result:{}", key, res.getResult().getLowercase());
	}


	@PostMapping(value="/{collection}/indexes",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)	
	public void createBulk(@PathVariable String collection, @RequestBody List<ReadmeDocument> docs)
			throws IOException, ReadmeParserException {
		log.debug("Bulk loading {} docs to collection:{}",docs.size(),collection);
		Map<String,String> mapping = fieldController.types(collection);
		BulkRequest req = new BulkRequest();
		for (ReadmeDocument doc : docs) {
			IndexRequest ireq = new IndexRequest(collection);			
			Map<String,Object> s = ReadmeMapAdapter.toMap(doc,mapping);										
			ireq.id(doc.getKey()).source(s);
			req.add(ireq);
		}
		BulkResponse bulkResponse = client.bulk(req, RequestOptions.DEFAULT);

		if (bulkResponse.hasFailures()) {
			for (BulkItemResponse bulkItemResponse : bulkResponse) {
				if (bulkItemResponse.isFailed()) {
					BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
					log.error("Failed to index bulk:{}", failure.getMessage());
				}
			}
			throw new IOException("Index bulk request has failures.");
		}
	}
	
	@GetMapping(value="/{collection}/indexes",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<StreamingResponseBody> readBulk(@PathVariable String collection, @RequestParam(defaultValue = "false") Boolean sysFields){		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (!sysFields) {
			String[] exFields = new String[] {"_*"};
			searchSourceBuilder.fetchSource(null,exFields);
			
		}
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());		
		SearchRequest searchRequest = new SearchRequest(collection);
		searchRequest.source(searchSourceBuilder);
	    StreamingResponseBody responseBody = response -> {
	    	response.write("[".getBytes());	    	
	    	SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			SearchHits hits = searchResponse.getHits();
			int n = 0;
			for (SearchHit sh : hits.getHits()) {
				if (n > 0) {
					response.write(",".getBytes());
				}
				response.write(sh.getSourceAsString().getBytes());
				n++;
			}
			response.write("]".getBytes());
	      };	      
	      return ResponseEntity.ok()
	              .contentType(MediaType.APPLICATION_JSON_UTF8)
	              .body(responseBody);
	     
	      
	}
	
			
	@DeleteMapping("/{collection}/indexes")
	public void deleteAll(@PathVariable String collection) throws IOException {
		log.debug("Delete index for collection:{}", collection);
		try {
			if (client.indices().exists(new GetIndexRequest(collection), RequestOptions.DEFAULT)) {
				client.indices().delete(new DeleteIndexRequest(collection), RequestOptions.DEFAULT);
			}
		} catch (ElasticsearchException e) {
			throw new IOException("Failed to delete index:" + collection);
		}
	}
		
}