package de.unibayreuth.bayceer.oc.controller;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.request.RequestParametersSnippet;

public class DocumentControllerApplicationTests extends ControllerApplicationTests {
	
		
	@Before
	public void setUp() throws IOException {
		super.setUp();
	}
	
	@After
	public void tearDown()  {
		super.tearDown();
	}
	
	@Test
	public void Create() throws IOException {
		given(web)
		.filter(
			document("index-post",
					pathParameters(
							parameterCollection,
							parameterKey							
					),						 
					requestFields(dcDocumentFields)																			
			)
		)
		.body(new String(Files.readAllBytes(Paths.get("src/test/resources/public_post.json"))))
		.post("/{collection}/index/{key}",PUB_COL,"99")
		.then()
		.statusCode(200);				
	}
		
			
	
		
	@Test
	public void Read() {
		given(web)
		.filter(
				document("document-get",
						pathParameters(
								parameterCollection,
								parameterKey
						),
						responseFields(dcDocumentFields)						
				)
		)
		.get("/{collection}/index/{key}",PUB_COL,"10").then().assertThat()
		.body("content",equalTo("id:10\ntitle:Secondary microplastics\ncreator:Lisa Simpson;Marge Simpson\npublisher:University of Calgary\ndate:2019/10/10"));
	}
	
	
	@Test
	public void ReadBulk() {
		given(web)
		.filter(
				document("indexes-get",
						pathParameters(
								parameterCollection								
						),
						requestParameters(
								parameterWithName("sysFields").description("Include system fields in output list: <'true'|'false'> default: 'false'").optional()
						)
				)
		)
		.param("sysFields","true")
		.get("/{collection}/indexes",PUB_COL);		
		
	}
	

			
	@Test
	public void Put() throws IOException {		
		given(web)
		.filter(
		document("index-put",
				pathParameters(
						parameterCollection,
						parameterKey	
				),
				requestFields(dcDocumentFields)																			
				)
		)
		.body(new String(Files.readAllBytes(Paths.get("src/test/resources/public_update.json"))))
		.put("/{collection}/index/{key}",PUB_COL,"2")
		.then()
		.statusCode(200);				
		given(web).get("/{collection}/index/{key}",PUB_COL,"2").then().assertThat().body("content",equalTo("id:2\ntitle:Ternary microplastics\ncreator:Marge Simpson\npublisher:University of Munich\ndate:2019/10/10"));	
	}
		
		
	@Test
	public void Delete() {
		given(web)
		.filter(document("index-delete",
				pathParameters(
						parameterCollection,
						parameterKey
				)
				
			)
		)
		.delete("/{collection}/index/{key}",PUB_COL,"2").then().statusCode(200);		
		given(web).get("/{collection}/index/{key}",PUB_COL,"2").then().statusCode(404);				
	}
	
	

}
