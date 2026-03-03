package com.inna.urlshortener.feature.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inna.urlshortener.TestcontainersConfiguration;
import com.inna.urlshortener.feature.users.UserRepository;
import java.time.LocalDateTime;
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
@Transactional
@Import(TestcontainersConfiguration.class)
class LinkRestControllerIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Helper: registration.
     */
    private void register(String username, String password) throws Exception {
        mockMvc.perform(post("/api/v1/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "%s",
                        "password": "%s"
                        }
                        """
                        .formatted(username, password)))
                .andExpect(status().isCreated());
    }

    /**
     * Helper: login and return JWT token.
     */
    private String authorizer_getToken(String username, String password) throws Exception {
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
    void createLink_shouldCreateLink_whenAuthorized() throws Exception {
        String username = "user1";
        String password = "Password1";

        register(username, password);

        String token = authorizer_getToken(username, password);

        mockMvc.perform(post("/api/v1/links")
                        .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "url":"https://google.com"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists());

        assertEquals(1, linkRepository.count());
    }

    @Test
    void createLink_shouldReturn401_whenNoToken() throws Exception {

        mockMvc.perform(post("/api/v1/links")
                        //.header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "url":"https://google.com"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createLink_shouldReturn400_whenUrlInvalid() throws Exception {
        String username = "user1";
        String password = "Password1";

        register(username, password);

        String token = authorizer_getToken("user1", "Password1");

        mockMvc.perform(post("/api/v1/links")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "url":"invalidUrl"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLinks_shouldReturnUserLinks() throws Exception {
        String username = "user3";
        String password = "Password1";

        register(username, password);

        Link link = new Link();
        link.setUrl("https://google.com");
        link.setShortLink("shortUrl");
        link.setUser(userRepository.findByUsername(username).get());
        linkRepository.save(link);

        String token = authorizer_getToken(username, password);

        mockMvc.perform(get("/api/v1/links")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortUrl").exists());
    }

    @Test
    void redirect_shouldReturn302_whenLinkExists() throws Exception {

        String username = "user4";
        String password = "Password1";

        register(username, password);

        Link link = new Link();
        link.setUrl("https://google.com");
        link.setShortLink("shortUrl");
        link.setUser(userRepository.findByUsername(username).get());
        linkRepository.save(link);

        mockMvc.perform(get("/api/v1/links/shortUrl"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://google.com"));
    }

    @Test
    void redirect_shouldReturn404_whenLinkNotExists() throws Exception {

        mockMvc.perform(get("/api/v1/links/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_shouldReturn404_whenLinkExpired() throws Exception {
        String username = "user4";
        String password = "Password1";

        register(username, password);

        Link link = new Link();
        link.setUrl("https://google.com");
        link.setShortLink("expired");
        link.setExpiresAt(LocalDateTime.now().minusDays(1));
        link.setUser(userRepository.findByUsername(username).get());

        linkRepository.save(link);

        mockMvc.perform(get("/api/v1/links/expired"))
                .andExpect(status().isGone());
    }

    @Test
    void redirect_shouldIncreaseOpenCount() throws Exception {
        String username = "user4";
        String password = "Password1";

        register(username, password);

        Link link = new Link();
        link.setShortLink("stat");
        link.setUrl("https://google.com");
        link.setOpenCount(0);
        link.setUser(userRepository.findByUsername(username).get());

        linkRepository.save(link);

        mockMvc.perform(get("/api/v1/links/stat"))
                .andExpect(status().isFound());

        Link updated = linkRepository.findByShortLink("stat").get();
        assertEquals(1, updated.getOpenCount());
    }

    @Test
    void deleteLink_shouldDelete_whenOwner() throws Exception {
        String username = "user4";
        String password = "Password1";

        register(username, password);

        Link link = new Link();
        link.setUrl("https://google.com");
        link.setShortLink("shortUrl");
        link.setUser(userRepository.findByUsername(username).get());
        linkRepository.save(link);

        String token = authorizer_getToken(username, password);

        mockMvc.perform(delete("/api/v1/links/shortUrl")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertEquals(0, linkRepository.count());
    }

    @Test
    void deleteLink_shouldReturn403_whenNotOwner() throws Exception {
        String usernameOwner = "owner";
        String usernameNotOwner = "notOwner";
        String password = "Password1";

        register(usernameOwner, password);
        register(usernameNotOwner, password);

        Link link = new Link();
        link.setUrl("https://google.com");
        link.setShortLink("shortUrl");
        link.setUser(userRepository.findByUsername(usernameOwner).get());
        linkRepository.save(link);

        String token = authorizer_getToken(usernameNotOwner, password);

        mockMvc.perform(delete("/api/v1/links/shortUrl")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        assertEquals(1, linkRepository.count());
    }
}
