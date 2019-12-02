package de.unibayreuth.bayceer.oc.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import org.junit.Test;


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
		.get("/{collection}/field/names",PUB_COL).then().assertThat()
		.body("$",hasItems("creator","publisher","title"));
	}
			

}
