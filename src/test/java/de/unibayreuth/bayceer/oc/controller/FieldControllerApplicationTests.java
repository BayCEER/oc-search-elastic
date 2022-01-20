package de.unibayreuth.bayceer.oc.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import org.junit.Test;
import org.springframework.restdocs.payload.JsonFieldType;


public class FieldControllerApplicationTests extends ControllerApplicationTests {
	
	@Test
	public void fields() {
		given(web)
		.filter(
				document("field-names",
						pathParameters(
								parameterCollection								
						),
						requestParameters(
								parameterWithName("sysFields").description("Include system fields in output list: <'true'|'false'> default: 'true'").optional()
						),
						responseFields(
								fieldWithPath("[]").description("Array of field names")
						)						
				)
		)
		.get("/{collection}/field/names",PUB_COL).then().assertThat()
		.body("$",hasItems("creator","publisher","title"));
	}
	
	@Test
	public void sysFields() {
		given(web)
		.filter(
				document("field-names",
						pathParameters(
								parameterCollection								
						),	
						requestParameters(
								parameterWithName("sysFields").description("Include system fields in output list: <'true'|'false'> default: 'true'").optional()
						),
						responseFields(
								fieldWithPath("[]").type(JsonFieldType.ARRAY).description("Array of field names")								
						)
				)
		)		
		.param("sysFields","true")
		.get("/{collection}/field/names",PUB_COL)
		.then().assertThat()
		.body("$",hasItems("_key","_path","_user","creator","publisher","title"));
	}
		
			

}
