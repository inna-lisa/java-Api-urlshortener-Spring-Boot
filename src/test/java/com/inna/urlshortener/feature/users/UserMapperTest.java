package com.inna.urlshortener.feature.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserMapperTest {

    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userMapper = new UserMapper(passwordEncoder);
    }

    @Test
    void fromDtoShouldMapUserRequestDtoToUser() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        User user = userMapper.toEntity(requestDto);

        assertEquals("testUser", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void toDtoShouldMapUserToUserResponseDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        String token = "jwt-token";

        UserResponseDto responseDto = userMapper.toDto(user, token);

        assertEquals(user.getId(), responseDto.getId());
        assertEquals(user.getUsername(), responseDto.getUsername());
        assertEquals(token, responseDto.getToken());
    }
}