package es.urjc.code.daw.library.rest.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import es.urjc.code.daw.library.book.Book;
import es.urjc.code.daw.library.book.BookService;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookController REST tests - MockMVC")
public class BookRestControllerUnitWTCTest {

	@Autowired
	private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        this.webTestClient = MockMvcWebTestClient
            .bindTo(mockMvc)
            .build();
    }

	@Test
	@DisplayName("Comprobar que se pueden recuperar todos los libros (como usuario sin logear)")
	public void getBooksTest() throws Exception{

        List<Book> fakeBooks = Arrays.asList(new Book("FAKE BOOK 1","Contenido de prueba"), new Book("FAKE BOOK 2","Contenido de prueba"));

        when(bookService.findAll()).thenReturn(fakeBooks);

        this.webTestClient
            .get()
                .uri("/api/books/")
            .exchange()
                .expectStatus()
                    .isOk()
                .expectBody()
                    .jsonPath("$.size()", 2)
        ;
		
	}

	@Test
    @DisplayName("Añadir un nuevo libro (como usuario logeado)")
    @WithMockUser(username = "user", password = "pass", roles = "USER")
	public void createBookTest() throws Exception {

        Book book = new Book("FAKE BOOK","Contenido de prueba");

        when(bookService.save(any(Book.class))).thenReturn(book);

        // CREAMOS UN NUEVO LIBRO
        
        this.webTestClient
            .post()
                .uri("/api/books/")
                .bodyValue(book)
            .exchange()
                .expectStatus()
                    .isCreated()
                .expectBody()
                    .jsonPath("$.title", book.getTitle())
            
        ;
          
	}

	@Test
    @DisplayName("Borrar un libro (como administrador)")
    @WithMockUser(username = "admin", password = "pass", roles = "ADMIN")
	public void deleteBookTest() throws Exception {

        //doNothing().when(bookService).delete(isA(Long.class));

        this.webTestClient
            .delete()
                .uri("/api/books/1")
            .exchange()
                .expectStatus()
                    .isOk();
        ;

	}

}
