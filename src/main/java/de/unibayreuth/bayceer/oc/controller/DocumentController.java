package de.unibayreuth.bayceer.oc.controller;

import java.io.IOException;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	public void indexDocument(@PathVariable String collection, @PathVariable String key, @RequestBody ReadmeDocument doc)
			throws IOException, ReadmeParserException {
		
		log.debug("Index doc:{} collection:{} path:{}", key, collection, doc.getPath());		
				
		Map<String,Object> s = ReadmeMapAdapter.toMap(doc,fieldController.types(collection));				
		IndexRequest req = new IndexRequest(collection);
		req.id(key);								
		req.source(s);				
		IndexResponse res = client.index(req, RequestOptions.DEFAULT);
		Result result = res.getResult();
		log.debug("Doc:{} Result:{}", key, result.getLowercase());
	}
	
	@GetMapping(value="/{collection}/index/{key}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ReadmeDocument getDocument(@PathVariable String collection, @PathVariable String key)
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
	public void updateDocument(@PathVariable String collection, @PathVariable String key, @RequestBody ReadmeDocument doc) throws IOException, ReadmeParserException {		
		indexDocument(collection, key, doc);		
	}

	

	@DeleteMapping(value="/{collection}/index/{key}")
	public void delete(@PathVariable String collection, @PathVariable String key) throws IOException {
		log.debug("Delete doc:{} collection:{}", key, collection);
		DeleteRequest req = new DeleteRequest(collection, key);
		DeleteResponse res = client.delete(req, RequestOptions.DEFAULT);
		log.debug("Doc:{} Result:{}", key, res.getResult().getLowercase());
	}


	@PostMapping(value="/{collection}/indexes",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)	
	public void indexBulk(@PathVariable String collection, @RequestBody List<ReadmeDocument> docs)
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