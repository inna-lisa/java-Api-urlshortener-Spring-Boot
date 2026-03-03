package com.inna.urlshortener.feature.users;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void validRegistration_shouldReturnResponseDto() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("testUser", "Password12");
        UserResponseDto userResponseDto = new UserResponseDto(1L, "testUser", null);

        when(userService.registration(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/api/v1/users/registration")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.token").isEmpty());
    }

    @Test
    void validRegistration_shouldReturnBadRequest_whenUsernameBlank() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("", "Password12");

        mockMvc.perform(post("/api/v1/users/registration")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validAuthorization_shouldReturnResponseDto() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("testUser", "Password12");
        UserResponseDto userResponseDto = new UserResponseDto(1L, "testUser", "mock-token");

        when(userService.authorization(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/api/v1/users/authorization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    void validAuthorization_shouldReturnBadRequest_whenPasswordBlank() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("testUser", "");

        mockMvc.perform(post("/api/v1/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest());
    }
}
