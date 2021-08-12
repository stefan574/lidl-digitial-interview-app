package ro.rentea.lidldigitalinterviewapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ro.rentea.lidldigitalinterviewapp.entity.Book;
import ro.rentea.lidldigitalinterviewapp.entity.Token;
import ro.rentea.lidldigitalinterviewapp.exception.BookAlreadyExistsException;
import ro.rentea.lidldigitalinterviewapp.exception.BookNotFoundException;
import ro.rentea.lidldigitalinterviewapp.exception.InvalidTokenException;
import ro.rentea.lidldigitalinterviewapp.repository.BookRepository;
import ro.rentea.lidldigitalinterviewapp.repository.TokenRepository;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class BookControllerIT {

    private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private Token token;

    @BeforeEach
    public void init() {
        token = tokenRepository.save(buildToken());
    }

    @Test
    @SneakyThrows
    void getAllBooks_tokenIsString_numberFormatException() {
        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "stringToken"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getCause()
                        instanceof NumberFormatException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to" +
                        " required type 'int'; nested exception is java.lang.NumberFormatException: For input string:"
                        + " \"stringToken\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void getAllBooks_tokenIsWrong_statusForbidden() {
        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "6789"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
                .andExpect(result -> assertEquals("Token 6789 is invalid.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void getBookById_tokenIsWrong_statusForbidden() {
        mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "6789"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
                .andExpect(result -> assertEquals("Token 6789 is invalid.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void createBook_tokenIsWrong_statusForbidden() {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildBook("title")))
                        .header("Authorization", "6789"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
                .andExpect(result -> assertEquals("Token 6789 is invalid.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void updateBook_tokenIsWrong_statusForbidden() {
        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildBook("title")))
                        .header("Authorization", "6789"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
                .andExpect(result -> assertEquals("Token 6789 is invalid.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void deleteCustomer_tokenIsWrong_statusForbidden() {
        mockMvc.perform(delete("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "6789"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
                .andExpect(result -> assertEquals("Token 6789 is invalid.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void getAllBooks_preparedListOfBooksInDb_preparedListOfBooksIsReturned() {
        Book book = bookRepository.save(buildBook("title"));

        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(book.getId()))
                .andExpect(jsonPath("$.[0].title").value(book.getTitle()));
    }

    @Test
    @SneakyThrows
    void getBookById_preparedBookInDb_preparedBookIsReturned() {
        Book book = bookRepository.save(buildBook("title"));

        mockMvc.perform(get("/books/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    @SneakyThrows
    void getBookById_noBookPreparedInDb_statusNotFound() {
        mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token.getToken()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookNotFoundException))
                .andExpect(result -> assertEquals("Could not find book with id: 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void createBook_preparedBook_preparedBookIsSavedInDb() {
        Book book = buildBook("title");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    @SneakyThrows
    void createBook_preparedBookInDb_statusConflict() {
        Book book = bookRepository.save(buildBook("title"));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookAlreadyExistsException))
                .andExpect(result -> assertEquals("Book with id " + book.getId() + " is already present.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void updateBook_preparedBookInDb_preparedBookIsUpdated() {
        Book dbBook = bookRepository.save(buildBook("title1"));

        Book book = buildBook("title2");

        mockMvc.perform(put("/books/" + dbBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dbBook.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    @SneakyThrows
    void updateBook_noPreparedBookInDb_preparedBookIsSaved() {
        Book book = buildBook("title");

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                        .header("Authorization", token.getToken()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    @SneakyThrows
    void deleteCustomer_preparedBookInDb_preparedBookIsDeleted() {
        Book book = bookRepository.save(buildBook("title"));

        mockMvc.perform(delete("/books/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8));

        mockMvc.perform(delete("/books/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token.getToken()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookNotFoundException))
                .andExpect(result -> assertEquals("Could not find book with id: " + book.getId() + ".",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void deleteCustomer_noPreparedBookInDb_statusNotFound() {
        mockMvc.perform(delete("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token.getToken()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookNotFoundException))
                .andExpect(result -> assertEquals("Could not find book with id: 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    private Book buildBook(String title) {
        Book book = new Book();
        book.setId(1L);
        book.setTitle(title);

        return book;
    }

    private Token buildToken() {
        Token tokenObject = new Token();
        tokenObject.setToken(1234);

        return tokenObject;
    }

}
