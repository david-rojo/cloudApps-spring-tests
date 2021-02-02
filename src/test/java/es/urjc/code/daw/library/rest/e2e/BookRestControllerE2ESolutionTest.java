package es.urjc.code.daw.library.rest.e2e;

/* Proposed solution */

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.urjc.code.daw.library.book.Book;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("BookController REST tests - RESTAssured")
public class BookRestControllerE2ESolutionTest {

	   @LocalServerPort
	    int port;

	    @BeforeEach
	    public void setUp() {
	        RestAssured.port = port;
	        RestAssured.useRelaxedHTTPSValidation();
	        RestAssured.baseURI = "https://localhost:"+port;
	    }

	    @Autowired
	    private ObjectMapper objectMapper;

	    @Test
		@DisplayName("Comprobar que se pueden recuperar todos los libros (como usuario sin logear)")
		public void getBooksTest() throws Exception{
	    	
	        when().
	            get("/api/books/").
	        then().
	            assertThat().
	                contentType(ContentType.JSON).
	                statusCode(200)

	        // No podemos comprobar el contenido de la respuesta:
	        // -> El siguiente test modifica la lista que obtendríamos
	        // -> Podríamos usar una base de datos distinta por cada test o asegurar que se limpian los datos creados en otros test
	        // -> Con una base de datos "limpia" solo podríamos comprobar que el contenido devuelto es un array vacío
	        //     .body("size()", equalTo(0))
	        ;
	    }

	    @Test
	    @DisplayName("Añadir un nuevo libro (como usuario logeado) y comprobar que se ha creado")
		public void createBookTest() throws Exception {

	        // CREAMOS UN NUEVO LIBRO

			Book book = new Book("FAKE BOOK","Contenido de prueba");
	    	
	        Book createdBook = 
	            given()
	                .auth()
	                    .basic("user", "pass")
	                .request()
	                    .body(objectMapper.writeValueAsString(book))
	                    .contentType(ContentType.JSON).
	            when()
	                .post("/api/books/").
	            then()
	                .assertThat()
	                .statusCode(201)
	                .body("title", equalTo(book.getTitle()))
	                .extract().as(Book.class);

	        // COMPROBAMOS QUE EL LIBRO SE HA CREADO CORRECTAMENTE

	        when()
	            .get("/api/books/{id}", createdBook.getId())
	        .then()
	             .assertThat()
	             .statusCode(200)
	             .body("title", equalTo(book.getTitle()));
			
	    
	    }

	    @Test
		@DisplayName("Borrar un libro (como administrador) comprobar que se ha borrado")
		public void deleteBookTest() throws Exception {

	        // CREAMOS UN NUEVO LIBRO

			Book book = new Book("FAKE BOOK","Contenido de prueba");
	    	
	        Book createdBook = 
	            given()
	                .auth()
	                    .basic("admin", "pass")
	                .request()
	                    .body(objectMapper.writeValueAsString(book))
	                    .contentType(ContentType.JSON)
	            .when()
	                .post("/api/books/")
	            .then()
	                .extract().as(Book.class);
	        
	        // BORRAMOS EL LIBRO CREADO
	        given()
	            .auth()
	                .basic("admin", "pass")
	        .when()
	             .delete("/api/books/{id}",createdBook.getId())
	        .then()
	             .assertThat()
	                .statusCode(200);

	        // COMPROBAMOS QUE EL LIBRO YA NO EXISTE

	        when()
	             .get("/api/books/{id}", createdBook.getId())
	        .then()
	             .assertThat()
	                .statusCode(404);

	    
	    }
}
