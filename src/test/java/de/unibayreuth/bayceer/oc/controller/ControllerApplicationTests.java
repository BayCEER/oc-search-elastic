package de.unibayreuth.bayceer.oc.controller;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public abstract class ControllerApplicationTests {

	private static final int ELASTIC_PORT = 9200;

	protected static final String PUB_COL = "oc-search-public";
	protected static final String PRI_COL = "oc-search-private";

	protected boolean CLEAN_EXIT = true;

	protected RequestSpecification web;
	protected RequestSpecification elastic;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	public final ParameterDescriptor parameterCollection = parameterWithName("collection").description("Collection identifier. Must start with 'oc' or 'owncloud'");	
	public final ParameterDescriptor parameterKey = parameterWithName("key").description("Document identifier");
	public final ParameterDescriptor parameterFileId = parameterWithName("id").description("File identifier");

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	protected FieldDescriptor[] dcDocumentFields = new FieldDescriptor[] {
			fieldWithPath("key").description("Unique document identifier").type(JsonFieldType.STRING),
			fieldWithPath("path").description("ownCloud file path").type(JsonFieldType.STRING),
			fieldWithPath("content").description("File content as string").type(JsonFieldType.STRING),
			fieldWithPath("lastModified").description("Last modification time").type(JsonFieldType.NUMBER).optional() };

	@Before
	public void setUp() throws IOException {

		web = new RequestSpecBuilder().setContentType("application/json").setAccept("application/json")
				.addFilter(documentationConfiguration(this.restDocumentation)).build();

		elastic = new RequestSpecBuilder().setContentType("application/json").setAccept("application/json")
				.setPort(ELASTIC_PORT).build();

		// Install readme template
		log.debug("Installing template.json");
		given(elastic).body(new String(Files.readAllBytes(Paths.get("src/test/resources/template.json"))))
				.put("_template/readme").then().statusCode(200);

		// Import dc data
		given(web)
				.filter(document("indexes-post",
						pathParameters(parameterWithName("collection").description("Collection identifier. Must start with 'oc' or 'owncloud'")),
						requestFields(fieldWithPath("[]").description("An array of documents")).andWithPrefix("[].",
								dcDocumentFields)))
				.body(new String(Files.readAllBytes(Paths.get("src/test/resources/public.json"))))
				.post("/{collection}/indexes", PUB_COL).then().statusCode(200);

		// Import dc2 data
		given(web).body(new String(Files.readAllBytes(Paths.get("src/test/resources/private.json"))))
				.post("/{collection}/indexes", PRI_COL).then().statusCode(200);

		// Wait on refresh
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}

	}

	@After
	public void tearDown() {		
		if (CLEAN_EXIT) {
			// Delete index
			given(web)
			.filter(
			document("indexes-delete",
					pathParameters(
							parameterCollection
														
					)																			
			)
			).delete("/{collection}/indexes", PUB_COL).then().statusCode(200);
			
			given(web).delete("/{collection}/indexes", PRI_COL).then().statusCode(200);
		}
	}

}
