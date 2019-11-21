package de.unibayreuth.bayceer.oc.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import io.restassured.response.Response;

public class ImageControllerApplicationTests extends ControllerApplicationTests {

	@After
	public void shutDown() throws IOException {
		if (CLEAN_EXIT) {
			given(web).filter(document("images-delete", pathParameters(parameterCollection)))
					.delete("/images/{collection}", "default").then().statusCode(200);

			given(web).filter(document("thumbs-delete", pathParameters(parameterCollection)))
					.delete("/thumbnails/{collection}", "default").then().statusCode(200);
		}
	}

	@Test
	public void thumbPost() {
		// POST
		given(web).contentType("image/png")
				.filter(document("thumb-post", pathParameters(parameterCollection, parameterKey)

				)).body(new File("src/test/resources/thumb.png")).post("/thumbnail/{collection}/{key}", "default", "6")
				.then().statusCode(200);

		// GET
		Response r = given(web).accept("image/png")
				.filter(document("thumb-get", pathParameters(parameterCollection, parameterKey))).when()
				.get("/thumbnail/{collection}/{key}", "default", "6").then().assertThat()
				.header("Content-Length", Integer::parseInt, equalTo(4093)).statusCode(200).extract().response();
		assertEquals(4093, r.body().asByteArray().length);

		// DELETE
		given(web).filter(document("thumb-delete", pathParameters(parameterCollection, parameterKey

		))).when().delete("/thumbnail/{collection}/{key}", "default", "6").then().statusCode(200);

		// NOT FOUND
		given(web).accept("image/png").when().get("/thumbnail/{collection}/{key}", "default", "6").then().statusCode(404);

	}

	@Test
	public void imagePost() {
		// POST
		given(web).contentType("image/png")
				.filter(document("image-post", pathParameters(parameterCollection, parameterKey)

				)).body(new File("src/test/resources/image.png")).post("/image/{collection}/{key}", "default", 10).then()
				.statusCode(200);

		// GET
		Response r = given(web).accept("image/png")
				.filter(document("image-get", pathParameters(parameterCollection, parameterKey

				))).when().get("/image/{collection}/{key}", "default", "10").then().assertThat()
				.header("Content-Length", Integer::parseInt, equalTo(13372342)).statusCode(200).extract().response();
		assertEquals(13372342, r.body().asByteArray().length);

		// DELETE
		given(web).filter(document("image-delete", pathParameters(parameterCollection, parameterKey))).when()
				.delete("image/{collection}/{key}", "default", "10").then().statusCode(200);

		// NOT FOUND
		given(web).accept("image/png").when().get("/image/{collection}/{key}", "default", "10").then().statusCode(404);

	}

}
