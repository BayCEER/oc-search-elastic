package de.unibayreuth.bayceer.oc.controller;

import org.junit.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;


public class FieldControllerApplicationTests extends ControllerApplicationTests {
	
	@Test
	public void fields() {
		given(web)
		.filter(
				document("field-names",
						pathParameters(
								parameterCollection								
						),	
						responseFields(
								fieldWithPath("[]").description("Array of field names")
						)
				)
		)
		.get("field/{collection}/names",PUB_COL).then().assertThat()
		.body("$",hasItems("creator","publisher","title"));
	}
	
	
	@Test
	public void cardinality() {		
		given(web)
		.filter(
				document("field-cardinality",
						pathParameters(
								parameterCollection								
						),
						responseFields(
								fieldWithPath("[]").description("Array of field information"),
								fieldWithPath("[].key").description("Field name").type(JsonFieldType.STRING),
								fieldWithPath("[].count").description("Field cardinality").type(JsonFieldType.NUMBER)
								
						)
				)
		)
		// TODO Check response !
		.get("field/{collection}/cardinality",PUB_COL).then().assertThat().statusCode(200);
		
	}

}
