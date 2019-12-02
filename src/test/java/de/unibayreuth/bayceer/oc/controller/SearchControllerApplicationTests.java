package de.unibayreuth.bayceer.oc.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import org.junit.Test;
import org.springframework.restdocs.payload.JsonFieldType;

public class SearchControllerApplicationTests extends ControllerApplicationTests {
					
	
	@Test 
	public void fullTextPaging() {
		String q = "microplastics";
		
		given(web).param("query", q).param("start", 0).param("hitsPerPage",5)
		.get("/{collection}/index",PUB_COL).then()
		.assertThat().body("hits.size()", is(5)).and().body("totalHits", equalTo(12));
						
		given(web).param("query", q).param("start", 5).param("hitsPerPage",5)
		.get("/{collection}/index",PUB_COL).then()
		.assertThat().body("hits.size()", is(5)).and().body("totalHits", equalTo(12));
		
		given(web).param("query", q).param("start", 10).param("hitsPerPage",5)
		.get("/{collection}/index",PUB_COL).then()
		.assertThat().body("hits.size()",is(2)).and().body("totalHits", equalTo(12));
		
	}
	
	@Test
	public void fullText() {
		given(web)
		.filter(
			document("index-get", 
				pathParameters(
						parameterCollection				
				),	
				requestParameters( 
						parameterWithName("query").description("https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-simple-query-string-query.html#simple-query-string-syntax[Simple query parser syntax]"), 
						parameterWithName("start").description("Start index"),
						parameterWithName("hitsPerPage").description("Number of hit records per page").optional(),
						parameterWithName("fragmentSize").description("Number of characters in each preview fragment").optional(),
						parameterWithName("fields").description("Field names for aggregation as JSON List e.g. \"['creator','publisher']").optional(),						
						parameterWithName("filter").description("Filter expression as JSON Map e.g.: {\"creator\":[\"Maggie Simpson\",\"Bart Simpson\"],...}").optional()
						
						
				), 
				responseFields( 
						subsectionWithPath("hits").description("An array of hits").type(JsonFieldType.ARRAY),
						fieldWithPath("hits[].key").description("File identifier").type(JsonFieldType.STRING),
						fieldWithPath("hits[].score").description("Match score").type(JsonFieldType.NUMBER),
						fieldWithPath("hits[].path").description("File path").type(JsonFieldType.STRING),
						fieldWithPath("hits[].previews").description("Hit highlighted text fragment").type(JsonFieldType.ARRAY),						
						fieldWithPath("hits[].thumb").description("Thumbnail").type(JsonFieldType.STRING).optional(),						
						fieldWithPath("totalHits").description("The number of all hits found").type(JsonFieldType.NUMBER),
						subsectionWithPath("aggs").description("An array of aggregation results").type(JsonFieldType.ARRAY).optional(),
						fieldWithPath("aggs[].key").description("Field key").type(JsonFieldType.STRING),
						fieldWithPath("aggs[].title").description("Field title with cardinality").type(JsonFieldType.STRING),
						fieldWithPath("aggs[].sumOtherDocCount").description("Number of documents not included").type(JsonFieldType.NUMBER),
						subsectionWithPath("aggs[].results").description("An array of count results").type(JsonFieldType.ARRAY).optional(),
						fieldWithPath("aggs[].results[].key").description("Key term").type(JsonFieldType.STRING),
						fieldWithPath("aggs[].results[].count").description("Counts of term").type(JsonFieldType.NUMBER)												
				)
			)
		)
		.param("query", "Maggie")
		.param("start", 0)
		.param("hitsPerPage", 10)
		.param("fragmentSize",5)
		.param("fields", "[\"creator\",\"publisher\"]")		
		.param("filter", "{\"creator\":[\"Maggie Simpson\",\"Bart Simpson\"]}")
				
		.get("/{collection}/index",PUB_COL).then().assertThat().body("hits.size()", is(2))
		.and().body("totalHits", equalTo(2));
	}
	
	

	

}
