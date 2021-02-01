package es.urjc.code.daw.library.rest.e2e;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("BookRestController e2e tests with RestAssured")
class BookRestControllerE2ETest {
	
	@LocalServerPort
    int port;
	
	@BeforeEach
	void setUp() {
		RestAssured.port = port;
		RestAssured.useRelaxedHTTPSValidation();
		RestAssured.baseURI = "https://localhost:" + port;
	}
	
	@Test
	@DisplayName("Get books test with not logged user, expected ok")
	void getBooksTest() throws Exception {
		
		//Given
		Response response1 = getBook(1);
		Response response2 = getBook(2);
		Response response3 = getBook(3);
		Response response4 = getBook(4);
		Response response5 = getBook(5);
		
		//When
        when().
            get("/api/books/").

        //Then
        then().
            statusCode(200).
            body(
    				"id", hasItems(
    						getId(response1),
    						getId(response2),
    						getId(response3),
    						getId(response4),
    						getId(response5)),
    				"title", hasItems(
    						getField(response1, "title"),
    						getField(response2, "title"),
    						getField(response3, "title"),
    						getField(response4, "title"),
    						getField(response5, "title")),
    				"description", hasItems(
    						getField(response1, "description"),
    						getField(response2, "description"),
    						getField(response3, "description"),
    						getField(response4, "description"),
    						getField(response5, "description"))
    				);
	}
	
	@Test
	@DisplayName("Post book test with logged user, expected ok")
	void postBookTest() throws Exception {
		
		//Given
		String title = "title1";
		String description = "description1";
		Response response = this.postBook(title, description);
				
		int id = this.getId(response);
		
		//When
		when()
			.get("/api/books/{id}", id)
				
		//Then
		.then()
			.statusCode(200)
			.body(
				"id", equalTo(id),
				"title", equalTo(title),
				"description",equalTo(description));
		
		this.deleteBook(id);		
	}
	
	@Test
	@DisplayName("Delete book test with admin user, expected ok")
	void deleteBookTest() throws Exception {	
	  
		//Given
		Response response = this.postBook("title2", "description2");
		int id = this.getId(response);
				
		given()
			.auth()
			.basic("admin", "pass")
		//When
		.when()
			.delete("/api/books/{id}", id)

		//Then	
		.then()
			.statusCode(200);
				
		when()
			.get("/api/books/{id}", id)
			.then()
			.statusCode(404);
	}
	
	private Response getBook(int id) {
		return given().
				contentType("application/json").
				when().
					get("/api/books/{id}", id).thenReturn();
	}
	
	private Response postBook(String title, String description) {
		return given()
				.auth()
				.basic("user", "pass")
				.contentType("application/json")
				.body("{"
						+ "\"title\":\"" + title + "\","
						+ "\"description\":\"" + description + "\""
						+ "}")
				.when()
				.post("/api/books/").andReturn();
	}
	
	private Response deleteBook(int id) {
		return given().
				contentType("application/json").
				when().
					delete("/api/books/{id}", id).thenReturn();
	}
	
	private String getField(Response response, String field) {
		return from(response.getBody().asString()).get(field);
	}
	
	private int getId(Response response) {
		return from(response.getBody().asString()).get("id");
	}
	
}
