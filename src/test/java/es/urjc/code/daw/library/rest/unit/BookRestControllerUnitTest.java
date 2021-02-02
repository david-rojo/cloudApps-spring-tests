package es.urjc.code.daw.library.rest.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.urjc.code.daw.library.book.Book;
import es.urjc.code.daw.library.book.BookService;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookRestController Unit tests using mocks")
class BookRestControllerUnitTest {

	@Autowired
    private MockMvc mvc;
	
	@MockBean
	private BookService bookService;

	@Autowired
	ObjectMapper objectMapper;
	
	Book bookToSave;
	
	Book bookSaved;
	
	List<Book> books;
	
	@BeforeEach
	void setup() {
		
		books = Arrays.asList(
				new Book("book1", "description1"),
				new Book("book2", "description2"));
		
		long id = 8L;
		String title = "title3";
		String description = "description3";
		bookToSave = new Book(title, description);
		bookSaved = new Book(title, description);
		bookSaved.setId(id);
	}
	
	@Test
	@DisplayName("Get books test with not logged user, expected ok")
	void getBooksTest() throws Exception {	    
		
	    when(bookService.findAll()).thenReturn(books);
		
	    mvc.perform(
	    		get("/api/books/")
	    		.contentType(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isOk())
	    	.andExpect(jsonPath("$", hasSize(2)))
	    	.andExpect(jsonPath("$[0].title", equalTo("book1")))
	    	.andExpect(jsonPath("$[0].description", equalTo("description1")))
	    	.andExpect(jsonPath("$[1].title", equalTo("book2")))
	    	.andExpect(jsonPath("$[1].description", equalTo("description2")));
	}
	
	@Test
	@DisplayName("Post book test with logged user, expected ok")
	@WithMockUser(username = "user", password = "pass", roles = "USER")
	void postBookTest() throws Exception {
		
	    when(bookService.save(Mockito.any())).thenReturn(bookSaved);
	    when(bookService.findOne(bookSaved.getId())).thenReturn(Optional.of(bookSaved));	    
	   
	    mvc.perform(
				post("/api/books/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookToSave))
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", equalTo(Math.toIntExact(bookSaved.getId()))))
			.andExpect(jsonPath("$.title", equalTo(bookToSave.getTitle())))
			.andExpect(jsonPath("$.description", equalTo(bookToSave.getDescription())));		
	}
	
	@Test
	@DisplayName("Delete book test with admin user, expected ok")
	@WithMockUser(username = "admin", password = "pass", roles = "ADMIN")
	void deleteBookTest() throws Exception {	
		
		doNothing().when(bookService).delete(isA(Long.class));
		
		mvc.perform(
				delete("/api/books/" + bookSaved.getId())
					.contentType(MediaType.APPLICATION_JSON)
				)
			.andExpect(status().isOk());		
	}
	
}
