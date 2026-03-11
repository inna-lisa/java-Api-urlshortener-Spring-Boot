package com.inna.urlshortener.feature.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inna.urlshortener.TestcontainersConfiguration;
import com.inna.urlshortener.feature.users.User;
import com.inna.urlshortener.feature.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfiguration.class)
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    private void createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private String getToken(String username, String password) throws Exception {

        MvcResult result = mockMvc.perform(post("/api/v1/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "username": "%s",
                                "password": "%s"
                                }
                                """
                                .formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);

        return json.get("token").asText();
    }

    @Test
    void accessProtectedResourceWithTokenShouldReturn200() throws Exception {

        createUser("userTest", "Password1");
        String token = getToken("userTest", "Password1");

        mockMvc.perform(get("/api/v1/links")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedResourceWithoutTokenShouldReturn401() throws Exception {

        mockMvc.perform(get("/api/v1/links"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedResourceWithoutInvalidTokenShouldReturn401() throws Exception {

        mockMvc.perform(get("/api/v1/links")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isUnauthorized());
    }
}
