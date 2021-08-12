package ro.rentea.lidldigitalinterviewapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.entity.Token;
import ro.rentea.lidldigitalinterviewapp.exception.CustomerAlreadyExistsException;
import ro.rentea.lidldigitalinterviewapp.exception.CustomerNotFoundException;
import ro.rentea.lidldigitalinterviewapp.exception.InvalidEmailAddressException;
import ro.rentea.lidldigitalinterviewapp.exception.TokenNotFoundException;
import ro.rentea.lidldigitalinterviewapp.repository.BookRepository;
import ro.rentea.lidldigitalinterviewapp.repository.CustomerRepository;
import ro.rentea.lidldigitalinterviewapp.repository.TokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class CustomerControllerIT {

    private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @SneakyThrows
    void getAllCustomers_preparedListOfCustomersInDb_preparedListOfCustomersIsReturned() {
        List<Customer> customers = new ArrayList<>();
        customers.add(buildCustomer("password".toCharArray(), "a@a.com"));
        Customer customer = customerRepository.save(customers.get(0));

        mockMvc.perform(get("/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(customer.getId()))
                .andExpect(jsonPath("$.[0].password").value(new String(customer.getPassword())))
                .andExpect(jsonPath("$.[0].emailAddress").value(customer.getEmailAddress()));
    }

    @Test
    @SneakyThrows
    void getCustomerById_preparedCustomerInDb_preparedCustomerIsReturned() {
        Customer customer = customerRepository.save(buildCustomer("password".toCharArray(), "a@a.com"));

        mockMvc.perform(get("/customers/" + customer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.password").value(new String(customer.getPassword())))
                .andExpect(jsonPath("$.emailAddress").value(customer.getEmailAddress()));
    }

    @Test
    @SneakyThrows
    void getCustomerById_noCustomer_statusNotFound() {
        mockMvc.perform(get("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomerNotFoundException))
                .andExpect(result -> assertEquals("Could not find customer with id: 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void createCustomer_preparedCustomer_customerAndTokenIsSavedInDb() {
        Customer customer = buildCustomer("password".toCharArray(), "a@a.com");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.password").value(new String(customer.getPassword())))
                .andExpect(jsonPath("$.emailAddress").value(customer.getEmailAddress()));

        Token dbToken = tokenRepository.findByEmailAddress(customer.getEmailAddress()).stream()
                .findFirst()
                .get();
        assertEquals(1L, dbToken.getId());
        assertArrayEquals(customer.getPassword(), dbToken.getPassword());
        assertEquals(customer.getEmailAddress(), dbToken.getEmailAddress());
    }

    @Test
    @SneakyThrows
    void createCustomer_preparedCustomerInDb_statusConflict() {
        Customer customer = customerRepository.save(buildCustomer("password".toCharArray(), "a@a.com"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof CustomerAlreadyExistsException))
                .andExpect(result -> assertEquals("Customer with id " + customer.getId()
                        + " is already present.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void createCustomer_faultyEmailAddress_statusBadRequest() {
        Customer customer = buildCustomer("password".toCharArray(), "emailAddress");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidEmailAddressException))
                .andExpect(result -> assertEquals("Email address " + customer.getEmailAddress()
                        + " is invalid.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void updateCustomer_preparedCustomerAndToken_customerAndTokenAreUpdated() {
        Customer dbCustomer = customerRepository.save(buildCustomer("password1".toCharArray(), "a@a.com"));
        Token dbToken = tokenRepository.save(buildToken("password1".toCharArray()));

        Customer customer = buildCustomer("password2".toCharArray(), "a@b.com");

        mockMvc.perform(put("/customers/" + dbCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dbCustomer.getId()))
                .andExpect(jsonPath("$.password").value(new String(customer.getPassword())))
                .andExpect(jsonPath("$.emailAddress").value(customer.getEmailAddress()));

        Token token = tokenRepository.findByEmailAddress(customer.getEmailAddress()).stream()
                .findFirst()
                .get();
        assertEquals(dbToken.getId(), token.getId());
        assertArrayEquals(customer.getPassword(), token.getPassword());
        assertEquals(customer.getEmailAddress(), token.getEmailAddress());
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void updateCustomer_noPreparedCustomerInDb_preparedCustomerIsSavedInDb() {
        Customer customer = buildCustomer("password2".toCharArray(), "a@b.com");

        mockMvc.perform(put("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.password").value(new String(customer.getPassword())))
                .andExpect(jsonPath("$.emailAddress").value(customer.getEmailAddress()));

        Token token = tokenRepository.findByEmailAddress(customer.getEmailAddress()).stream()
                .findFirst()
                .get();
        assertEquals(1L, token.getId());
        assertArrayEquals(customer.getPassword(), token.getPassword());
        assertEquals(customer.getEmailAddress(), token.getEmailAddress());
    }

    @Test
    @SneakyThrows
    void updateCustomer_preparedCustomerButNoToken_statusNotFound() {
        Customer dbCustomer = customerRepository.save(buildCustomer("password1".toCharArray(), "a@a.com"));

        Customer customer = buildCustomer("password2".toCharArray(), "a@b.com");

        mockMvc.perform(put("/customers/" + dbCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof TokenNotFoundException))
                .andExpect(result -> assertEquals("Could not find token for email address "
                                + dbCustomer.getEmailAddress() + ".",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void deleteCustomer_preparedCustomerAndTokenInDb_preparedCustomerIsDeleted() {
        Customer customer = customerRepository.save(buildCustomer("password".toCharArray(), "a@a.com"));
        Token token = tokenRepository.save(buildToken("password".toCharArray()));

        mockMvc.perform(delete("/customers/" + customer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8));

        mockMvc.perform(get("/customers/" + customer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomerNotFoundException))
                .andExpect(result -> assertEquals("Could not find customer with id: " + customer.getId()
                        + ".", Objects.requireNonNull(result.getResolvedException()).getMessage()));

        assertFalse(tokenRepository.findByEmailAddress(token.getEmailAddress()).stream()
                .findFirst()
                .isPresent());
    }

    @Test
    @SneakyThrows
    void deleteCustomer_noCustomerInDb_statusNotFound() {
        mockMvc.perform(delete("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomerNotFoundException))
                .andExpect(result -> assertEquals("Could not find customer with id: 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void deleteCustomer_preparedCustomerButNoTokenInDb_statusNotFound() {
        Customer customer = customerRepository.save(buildCustomer("password".toCharArray(), "a@a.com"));

        mockMvc.perform(delete("/customers/" + customer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof TokenNotFoundException))
                .andExpect(result -> assertEquals("Could not find token for email address "
                                + customer.getEmailAddress() + ".",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    private Customer buildCustomer(char[] password, String emailAddress) {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setPassword(password);
        customer.setEmailAddress(emailAddress);

        return customer;
    }

    private Token buildToken(char[] password) {
        Token tokenObject = new Token();
        tokenObject.setPassword(password);
        tokenObject.setEmailAddress("a@a.com");

        return tokenObject;
    }

}
