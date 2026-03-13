package com.inna.urlshortener.feature.links;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inna.urlshortener.feature.exceptions.LinkNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LinkRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class LinkRestControllerTest {

    @MockitoBean
    private LinkServiceImpl linkService;

    @Mock
    private Principal principal;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LinkResponseDto linkResponseDto;
    private LinkCreateRequestDto linkCreateRequestDto;
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        linkCreateRequestDto = new LinkCreateRequestDto();
        linkCreateRequestDto.setUrl("https://testurl.com");

        linkResponseDto = new LinkResponseDto();
        linkResponseDto.setShortUrl("shortUrl");
        linkResponseDto.setUrl("https://testurl.com");
        linkResponseDto.setOpenCount(0);
        linkResponseDto.setCreatedAt(LocalDateTime.now());
        linkResponseDto.setExpiresAt(LocalDateTime.now().plusDays(30));
        linkResponseDto.setUsername(username);
    }

    @Test
    void createShouldReturnCreatedLink() throws Exception {
        when(principal.getName()).thenReturn(linkResponseDto.getUsername());
        when(linkService.create(any(LinkCreateRequestDto.class), anyString())).thenReturn(linkResponseDto);

        mockMvc.perform(post("/api/v1/links")
                        .principal(principal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value(linkResponseDto.getShortUrl()))
                .andExpect(jsonPath("$.url").value(linkResponseDto.getUrl()))
                .andExpect(jsonPath("$.openCount").value(linkResponseDto.getOpenCount()))
                .andExpect(jsonPath("$.username").value(linkResponseDto.getUsername()));

        verify(linkService).create(any(LinkCreateRequestDto.class), anyString());
    }

    @Test
    void createShouldReturnBadRequestWhenInvalidBody() throws Exception {

        mockMvc.perform(post("/api/v1/links")
                        .principal(principal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(linkService, never()).create(any(), any());
    }

    @Test
    void redirectShouldReturnFoundWithLocationHeader() throws Exception {
        when(linkService.getLink(linkResponseDto.getShortUrl())).thenReturn(linkResponseDto.getUrl());

        mockMvc.perform(get("/api/v1/links/shortUrl")
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", linkResponseDto.getUrl()));

        verify(linkService).getLink(linkResponseDto.getShortUrl());
    }

    @Test
    void redirectShouldReturnNotFoundWhenLinkNotExists() throws Exception {
        when(linkService.getLink("unknown")).thenThrow(new LinkNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/links/unknown")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserLinksShouldReturnLinks() throws Exception {
        when(principal.getName()).thenReturn(linkResponseDto.getUsername());
        when(linkService.getUserLinks(username)).thenReturn(List.of(linkResponseDto));

        mockMvc.perform(get("/api/v1/links")
                        .principal(principal)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortUrl").value(linkResponseDto.getShortUrl()))
                .andExpect(jsonPath("$[0].url").value(linkResponseDto.getUrl()))
                .andExpect(jsonPath("$[0].username").value(linkResponseDto.getUsername()));

        verify(linkService).getUserLinks(username);
    }

    @Test
    void getActiveUserLinksShouldReturnLinksByUser() throws Exception {
        when(principal.getName()).thenReturn(linkResponseDto.getUsername());
        when(linkService.getActiveUserLinks(username)).thenReturn(List.of(linkResponseDto));

        mockMvc.perform(get("/api/v1/links/active")
                        .principal(principal)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortUrl").value(linkResponseDto.getShortUrl()))
                .andExpect(jsonPath("$[0].url").value(linkResponseDto.getUrl()))
                .andExpect(jsonPath("$[0].username").value(linkResponseDto.getUsername()));

        verify(linkService).getActiveUserLinks(username);
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        when(principal.getName()).thenReturn(linkResponseDto.getUsername());
        doNothing().when(linkService).delete(linkResponseDto.getShortUrl(), username);

        mockMvc.perform(delete("/api/v1/links/shortUrl")
                        .principal(principal)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(linkService).delete(linkResponseDto.getShortUrl(), username);
    }
    @Test
    void updateShouldReturnUpdatedLink() throws Exception {

        LinkUpdateRequestDto dto = new LinkUpdateRequestDto();
        dto.setUrl("https://new.com");

        when(principal.getName()).thenReturn(username);
        when(linkService.update(anyString(), any(), anyString())).thenReturn(linkResponseDto);

        mockMvc.perform(patch("/api/v1/links/shortUrl")
                        .principal(principal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(linkResponseDto.getShortUrl()))
                .andExpect(jsonPath("$.url").value(linkResponseDto.getUrl()));

        verify(linkService).update(anyString(), any(), anyString());
    }

    @Test
    void updateShouldReturnBadRequestWhenBodyInvalid() throws Exception {

        mockMvc.perform(patch("/api/v1/links/shortUrl")
                        .principal(principal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturnBadRequestWhenDateInvalid() throws Exception {

        mockMvc.perform(patch("/api/v1/links/shortUrl")
                        .principal(principal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "expiresAt":"invalid-date"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
