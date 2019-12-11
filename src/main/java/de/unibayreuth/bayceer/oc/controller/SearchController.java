package de.unibayreuth.bayceer.oc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.unibayreuth.bayceer.oc.controller.ImageController.ImageType;
import de.unibayreuth.bayceer.oc.entity.AggResponse;
import de.unibayreuth.bayceer.oc.entity.Hit;
import de.unibayreuth.bayceer.oc.entity.ReadmeDocument;
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
	private static final String HIGHLIGHT_TAG = "mark";

	@GetMapping(value = "/{collection}/index", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Response search(@PathVariable String collection,
			@RequestParam(value = "query", defaultValue = "") String queryString,
			@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "hitsPerPage", defaultValue = "10") int hitsPerPage,
			@RequestParam(value = "fragmentSize", defaultValue = "20") int fragmentSize,
			@RequestParam(value = "fields", defaultValue = EMPTY_ARRAY) String fields, // ['creator','publisher']
			@RequestParam(value = "filter", defaultValue = EMPTY_MAP) String filter // {"creator":["Maggie
																					// Simpson","Bart
																					// Simpson"],"publisher":[]}]
	) throws ParseException, IOException {

		log.debug("Collection:{} Query:{} Start:{} HitsPerPage:{} FragmentSize:{} Fields:{} Filter:{}", collection,
				queryString, start, hitsPerPage, fragmentSize, fields, filter);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.from(start);
		searchSourceBuilder.size(hitsPerPage);

		// Query
		searchSourceBuilder.query(getQueryBuilder(queryString, filter));

		// Aggregations
		for (AggregationBuilder a : getAggregationBuilders(fields)) {
			searchSourceBuilder.aggregation(a);
		}
		// Highlight
		searchSourceBuilder.highlighter(getHighlighter(fragmentSize));

		// Search
		SearchRequest searchRequest = new SearchRequest(collection);
		searchRequest.source(searchSourceBuilder);

		// Response
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();

		Response r = new Response();
		r.setTotalHits(hits.getTotalHits().value);
		r.setHits(new ArrayList<Hit>(hitsPerPage));
		for (SearchHit sh : hits.getHits()) {
			String key = sh.getId();
			// Previews
			List<String> previews = new ArrayList<String>();
			sh.getHighlightFields().forEach((f, v) -> {
				if (!(f.startsWith(ReadmeDocument.SYSTEM_FIELD_PREFIX) || f.endsWith(".keyword"))) {
					for (Text t : v.fragments()) {
						previews.add(t.string());
					}
				}
			});

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
				Terms ta = ((Terms) a);
				AggResponse ar = new AggResponse(a.getName(), ta.getSumOfOtherDocCounts());
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

	@GetMapping(value = "/{collection}/terms", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<String> terms(@PathVariable String collection,
			@RequestParam(value = "query", defaultValue = "") String queryString,
			@RequestParam(value = "filter", defaultValue = EMPTY_MAP) String filter,
			@RequestParam(value = "maxHits", defaultValue = "10") int maxHits,
			@RequestParam(value = "fragmentSize", defaultValue = "20") int fragmentSize) throws IOException {

		log.debug("Collection:{} Query:{} Filter:{} MaxHits:{}", collection, queryString, filter, maxHits);
		Pattern emPattern = Pattern.compile(String.format("<%s>(.+)</%s>",HIGHLIGHT_TAG,HIGHLIGHT_TAG));
			
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.size(maxHits);
		searchSourceBuilder.query(getQueryBuilder(queryString, filter));
		searchSourceBuilder.highlighter(getHighlighter(fragmentSize));
		SearchRequest searchRequest = new SearchRequest(collection);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();

		Map<String, Integer> matches = new HashMap<String, Integer>();
		for (SearchHit sh : hits.getHits()) {
			sh.getHighlightFields().forEach((f, v) -> {
				if (!(f.startsWith(ReadmeDocument.SYSTEM_FIELD_PREFIX) || f.endsWith(".keyword"))) {
					for (Text t : v.getFragments()) {
						Matcher match = emPattern.matcher(t.string());
						while (match.find()) {
							String m = match.group(1);
							Integer c = matches.get(m);
							matches.put(m, (c == null) ? 0 : c++);
						}
					}
				}
			});
		}
		List<String> ret = matches.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(maxHits).collect(Collectors.mapping(Map.Entry::getKey, Collectors.toList()));
		return ret;
	}

	private HighlightBuilder getHighlighter(int fragmentSize) {
		HighlightBuilder hb = new HighlightBuilder();
		hb.preTags(String.format("<%s>", HIGHLIGHT_TAG));
		hb.postTags(String.format("</%s>", HIGHLIGHT_TAG));
		HighlightBuilder.Field hc = new HighlightBuilder.Field("*");
		hc.fragmentSize(fragmentSize);
		hb.field(hc);
		return hb;
	}

	private List<AggregationBuilder> getAggregationBuilders(String fields)
			throws JsonParseException, JsonMappingException, IOException {
		List<AggregationBuilder> ret = new ArrayList<AggregationBuilder>();
		if (!fields.equals(EMPTY_ARRAY)) {
			ObjectMapper om = new ObjectMapper();
			JavaType type = om.getTypeFactory().constructCollectionType(List.class, String.class);
			List<String> l = om.readValue(fields, type);
			for (String nf : l) {
				TermsAggregationBuilder ag = AggregationBuilders.terms(nf).field(nf + ".keyword");
				ag.size(TERM_BUCKET_SIZE);
				ret.add(ag);
			}
		}
		return ret;
	}

	private QueryBuilder getQueryBuilder(String queryString, String filter)
			throws JsonParseException, JsonMappingException, IOException {
		QueryBuilder q = (queryString.isEmpty()) ? QueryBuilders.matchAllQuery()
				: QueryBuilders.simpleQueryStringQuery(queryString);
		if (filter.equals(EMPTY_MAP)) {
			return q;
		} else {
			BoolQueryBuilder bq = QueryBuilders.boolQuery().must(q);
			ObjectMapper om = new ObjectMapper();
			Map<String, List<String>> map = om.readValue(filter, new TypeReference<Map<String, List<String>>>() {
			});
			map.forEach((key, value) -> {
				bq.filter((value.isEmpty()) ? QueryBuilders.matchAllQuery()
						: QueryBuilders.termsQuery(key + ".keyword", value));
			});
			return bq;
		}
	}

}
