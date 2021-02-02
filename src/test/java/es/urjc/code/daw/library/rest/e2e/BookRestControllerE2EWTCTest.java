package es.urjc.code.daw.library.rest.e2e;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import es.urjc.code.daw.library.book.Book;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("BookController REST tests - WebTestClient")
public class BookRestControllerE2EWTCTest {

	@Autowired
    private WebTestClient webTestClient;

    @Test
	@DisplayName("Comprobar que se pueden recuperar todos los libros (como usuario sin logear)")
	public void getBooksTest() throws Exception{

        this.webTestClient
            .get()
                .uri("/api/books/")
            .exchange()
                .expectStatus()
                    .isOk()
        ;
    }

    @Test
    @DisplayName("Añadir un nuevo libro (como usuario logeado) y comprobar que se ha creado")
	public void createBookTest() throws Exception {

        // CREAMOS UN NUEVO LIBRO

        Book book = new Book("FAKE BOOK","Contenido de prueba");
        
        Book createdBook = this.webTestClient
            .mutate()
                .filter(basicAuthentication("user", "pass")).build()
            .post()
                .uri("/api/books/")
                .bodyValue(book)
            .exchange()
                .expectStatus()
                    .isCreated()
                .returnResult(Book.class)
                    .getResponseBody().single().block()
            
        ;


        // COMPROBAMOS QUE EL LIBRO SE HA CREADO CORRECTAMENTE

        this.webTestClient
            .get()
                .uri("/api/books/"+createdBook.getId())
            .exchange()
                .expectStatus()
                    .isOk()
                .expectBody()
                    .jsonPath("$.title", book.getTitle())
        ;
		
    
    }

    @Test
	@DisplayName("Borrar un libro (como administrador) comprobar que se ha borrado")
	public void deleteBookTest() throws Exception {

        // CREAMOS UN NUEVO LIBRO

        Book book = new Book("FAKE BOOK","Contenido de prueba");
        
        Book createdBook = this.webTestClient
            .mutate()
                .filter(basicAuthentication("admin", "pass")).build()
            .post()
                .uri("/api/books/")
                .bodyValue(book)
            .exchange()
                .expectStatus()
                    .isCreated()
                .returnResult(Book.class)
                    .getResponseBody().single().block()
            
        ;
    	
        
        // BORRAMOS EL LIBRO CREADO

        this.webTestClient
            .mutate()
                    .filter(basicAuthentication("admin", "pass")).build()
            .delete()
                .uri("/api/books/"+createdBook.getId())
            .exchange()
                .expectStatus()
                    .isOk();
        ;


        // COMPROBAMOS QUE EL LIBRO YA NO EXISTE

        this.webTestClient
            .get()
                .uri("/api/books/"+createdBook.getId())
            .exchange()
                .expectStatus()
                    .isNotFound();
        ;

    
    }

}
