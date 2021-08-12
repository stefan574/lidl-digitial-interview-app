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
import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.entity.Token;
import ro.rentea.lidldigitalinterviewapp.exception.IncorrectEmailAddressOrPasswordException;
import ro.rentea.lidldigitalinterviewapp.repository.CustomerRepository;
import ro.rentea.lidldigitalinterviewapp.repository.TokenRepository;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class TokenControllerIT {

    private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void init() {
        tokenRepository.save(buildToken());
    }

    @Test
    @SneakyThrows
    void getTokenByEmailAddressAndPassword_preparedCustomerAndTokenInDb_preparedTokenIsReturnedAndSavedInDb() {
        Customer customer = customerRepository.save(buildCustomer("password".toCharArray(), "a@a.com"));

        assertNull(tokenRepository.findByEmailAddress(customer.getEmailAddress()).get(0).getToken());

        mockMvc.perform(get("/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber());

        assertNotNull(tokenRepository.findByEmailAddress(customer.getEmailAddress()).get(0).getToken());
    }

    @Test
    @SneakyThrows
    void getTokenByEmailAddressAndPassword_emailAddressIncorrect_statusUnauthorized() {
        Customer customer = customerRepository.save(buildCustomer("password".toCharArray(), "b@a.com"));

        mockMvc.perform(get("/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectEmailAddressOrPasswordException))
                .andExpect(result -> assertEquals("Email address or password are incorrect.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void getTokenByEmailAddressAndPassword_passwordIncorrect_statusUnauthorized() {
        Customer customer = customerRepository.save(buildCustomer("faultyPassword".toCharArray(), "a@a.com"));

        mockMvc.perform(get("/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectEmailAddressOrPasswordException))
                .andExpect(result -> assertEquals("Email address or password are incorrect.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void getTokenByEmailAddressAndPassword_passwordAndEmailAddressAreIncorrect_statusUnauthorized() {
        Customer customer = customerRepository.save(buildCustomer("faultyPassword".toCharArray(), "b@a.com"));

        mockMvc.perform(get("/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectEmailAddressOrPasswordException))
                .andExpect(result -> assertEquals("Email address or password are incorrect.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    private Customer buildCustomer(char[] password, String emailAddress) {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setPassword(password);
        customer.setEmailAddress(emailAddress);

        return customer;
    }

    private Token buildToken() {
        Token tokenObject = new Token();
        tokenObject.setPassword("password".toCharArray());
        tokenObject.setEmailAddress("a@a.com");

        return tokenObject;
    }

}
