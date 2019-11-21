package de.unibayreuth.bayceer.oc.controller;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	public void Post() throws IOException {
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
		.post("/index/{collection}/{key}",PUB_COL,"99")
		.then()
		.statusCode(200);				
	}
		
			
	
	@Test
	public void Get() {
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
		.get("index/{collection}/{key}",PUB_COL,"10").then().assertThat()
		.body("content",equalTo("title:Secondary microplastics\ncreator:Lisa Simpson\npublisher:University of Calgary\n"));
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
		.put("/index/{collection}/{key}",PUB_COL,"2")
		.then()
		.statusCode(200);				
		given(web).get("/index/{collection}/{key}",PUB_COL,"2").then().assertThat().body("content",equalTo("title:Ternary microplastics\ncreator:Marge Simpson\npublisher:University of Munich\n"));	
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
		.delete("/index/{collection}/{key}",PUB_COL,"2").then().statusCode(200);		
		given(web).get("/index/{collection}/{key}",PUB_COL,"2").then().statusCode(404);				
	}
	
	

}
