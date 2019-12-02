package de.unibayreuth.bayceer.oc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.unibayreuth.bayceer.oc.controller.ImageController.ImageType;
import de.unibayreuth.bayceer.oc.entity.AggResponse;
import de.unibayreuth.bayceer.oc.entity.Hit;
import de.unibayreuth.bayceer.oc.entity.Preview;
import de.unibayreuth.bayceer.oc.entity.PreviewAdapter;
import de.unibayreuth.bayceer.oc.entity.Response;
import de.unibayreuth.bayceer.oc.entity.TermCount;

@RestController
public class SearchController {
	
	@Autowired
	private ImageController imageController;

	@Autowired
	RestHighLevelClient client;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String EMPTY_ARRAY = "[]";
	private static final String EMPTY_MAP = "{}";
	private static final int TERM_BUCKET_SIZE = 10;


	@GetMapping(value="/{collection}/index",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Response search(@PathVariable String collection,
			@RequestParam(value = "query", defaultValue = "") String queryString,
			@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "hitsPerPage", defaultValue = "10") int hitsPerPage,
			@RequestParam(value = "fragmentSize", defaultValue = "20") int fragmentSize,
			@RequestParam(value = "fields", defaultValue=EMPTY_ARRAY) String fields, // ['creator','publisher']						
			@RequestParam(value = "filter", defaultValue=EMPTY_MAP) String filter // {"creator":["Maggie Simpson","Bart Simpson"],"publisher":[]}]
	) throws ParseException, IOException {

		log.debug("Collection:{} Query:{} Start:{} HitsPerPage:{} FragmentSize:{} Fields:{} Filter:{}",
				collection, queryString, start, hitsPerPage, fragmentSize, fields, filter);

		// Query
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.from(start);
		searchSourceBuilder.size(hitsPerPage);

		QueryBuilder q = (queryString.isEmpty()) ? QueryBuilders.matchAllQuery()
				: QueryBuilders.simpleQueryStringQuery(queryString).field("_content.search");
		
		// Filter
		if (filter.equals(EMPTY_MAP)) {
			searchSourceBuilder.query(q);
		} else {
			BoolQueryBuilder bq = QueryBuilders.boolQuery().must(q);
			ObjectMapper om = new ObjectMapper();
			Map<String, List<String>> map = om.readValue(filter, new TypeReference<Map<String, List<String>>>() {});		
			map.forEach((key,value) -> {
				bq.filter( (value.isEmpty())?QueryBuilders.matchAllQuery(): QueryBuilders.termsQuery(key + ".keyword", value));	
			});			
			searchSourceBuilder.query(bq);
		}

		// Aggregations
		if (!fields.equals(EMPTY_ARRAY)) {				
			ObjectMapper om = new ObjectMapper();
			JavaType type = om.getTypeFactory().constructCollectionType(List.class,String.class);			
			List<String> l = om.readValue(fields, type);			
			for (String nf : l){			
				TermsAggregationBuilder ag = AggregationBuilders.terms(nf).field(nf + ".keyword");
				ag.size(TERM_BUCKET_SIZE);
				searchSourceBuilder.aggregation(ag);
			}
		}

		// Highlight
		HighlightBuilder hb = new HighlightBuilder();
		HighlightBuilder.Field hc = new HighlightBuilder.Field("_content.search");				
		hc.fragmentSize(fragmentSize);
		hb.field(hc);
		searchSourceBuilder.highlighter(hb);

		SearchRequest searchRequest = new SearchRequest(collection);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();

		Response r = new Response();
		r.setTotalHits(hits.getTotalHits().value);
		r.setHits(new ArrayList<Hit>(hitsPerPage));
		for (SearchHit sh : hits.getHits()) {
			String key = sh.getId();
			// Previews
			List<Preview> previews = new ArrayList<Preview>();
			Map<String, HighlightField> hf = sh.getHighlightFields();
			if (hf.containsKey("_content.search")) {
				for (Text f : hf.get("_content.search").fragments()) {										
					previews.add(PreviewAdapter.fromString(f.string()));
				}
				;
			}

			// Thumb
			byte[] thumb = imageController.exits(collection, key, ImageType.THUMBNAIL)
					? imageController.getThumb(collection, key)
					: null;

			// Source Fields
			Map<String, Object> sm = sh.getSourceAsMap();

			// Path
			String path = (String) sm.get("_path");

			// Add Response
			r.getHits().add(new Hit(key, Float.valueOf(sh.getScore()), path, previews, thumb));

		}

		// Read Aggregations
		List<AggResponse> ares = new ArrayList<AggResponse>();
		if (!fields.equals(EMPTY_ARRAY)) {			
			for (Aggregation a : searchResponse.getAggregations()) {
				Terms ta = ((Terms)a);						
				AggResponse ar = new AggResponse(a.getName(),ta.getSumOfOtherDocCounts());															
				for (Bucket b : ta.getBuckets()) {					
					ar.getResults().add(new TermCount(b.getKeyAsString(), b.getDocCount()));
				}
				ar.buildTitle();
				ares.add(ar);				
			}
		}
		r.setAggs(ares);
		return r;
	}

}
