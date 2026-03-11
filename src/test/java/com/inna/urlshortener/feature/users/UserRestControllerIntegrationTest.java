package com.inna.urlshortener.feature.users;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class UserRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15")
            .withDatabaseName("testDb")
            .withUsername("testUser")
            .withPassword("testPass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void registrationShouldCreateUserInDataBase() throws Exception {

        String request = """
                {
                "username": "integrationUser",
                "password": "Password1"
                }
                """;

        mockMvc.perform(post("/api/v1/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integrationUser"));

        Optional<User> user = userRepository.findByUsername("integrationUser");

        assertTrue(user.isPresent());
        assertNotEquals("Password1", user.get().getPassword());
    }

    @Test
    void registrationShouldReturnConflictWhenUserExists() throws Exception {

        User user = new User();
        user.setUsername("exists");
        user.setPassword("encoded");
        userRepository.save(user);

        String request = """
                {
                "username": "exists",
                "password": "Password1"
                }
                """;

        mockMvc.perform(post("/api/v1/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict());
    }

    @Test
    void registrationShouldReturnBadRequestWhenPasswordInvalid() throws Exception {

        String request = """
                {
                "username": "user",
                "password": "123"
                }
                """;

        mockMvc.perform(post("/api/v1/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertTrue(userRepository.findByUsername("user").isEmpty());
    }

    @Test
    void authorizationShouldReturnTokenWhenCredentialsValid() throws Exception {

        User user = new User();
        user.setUsername("authUser");
        user.setPassword(passwordEncoder.encode("Password1"));
        userRepository.save(user);

        String request = """
                {
                "username": "authUser",
                "password": "Password1"
                }
                """;

        mockMvc.perform(post("/api/v1/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void authorizationShouldReturnNotFoundWhenUserNotExists() throws Exception {

        String request = """
                {
                "username": "noUser",
                "password": "Password1"
                }
                """;

        mockMvc.perform(post("/api/v1/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    void authorizationShouldReturnBadRequestWhenPasswordWrong() throws Exception {

        User user = new User();
        user.setUsername("authUser");
        user.setPassword(passwordEncoder.encode("encoded"));
        userRepository.save(user);

        String request = """
                {
                "username": "authUser",
                "password": "wrong"
                }
                """;

        mockMvc.perform(post("/api/v1/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
