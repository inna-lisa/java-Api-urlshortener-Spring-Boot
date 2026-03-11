package com.inna.urlshortener.feature.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.inna.urlshortener.feature.exceptions.JwtAuthenticationException;
import com.inna.urlshortener.feature.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private User user;
    private final String secret = "mySuperSecretKeymySuperSecretKeymySuperSecretKey";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        jwtProvider = new JwtProvider(secret, 1000);
    }

    @Test
    void generateTokenShouldReturnValidToken() {
        String token = jwtProvider.generateToken(user.getId(), user.getUsername());

        assertNotNull(token);
        assertEquals(user.getUsername(), jwtProvider.getUsername(token));
    }

    @Test
    void validateTokenShouldPassWhenTokenValid() {
        String token = jwtProvider.generateToken(user.getId(), user.getUsername());

        assertDoesNotThrow(() -> jwtProvider.validateToken(token));
    }

    @Test
    void validateTokenShouldThrowWhenTokenExpired() throws InterruptedException {

        JwtProvider shortLiveProvider = new JwtProvider(secret, 1);

        String token = shortLiveProvider.generateToken(user.getId(), user.getUsername());

        Thread.sleep(5);

        assertThrows(JwtAuthenticationException.class, () -> shortLiveProvider.validateToken(token));
    }

    @Test
    void validateTokenShouldThrowWhenTokenMalformed() {

        assertThrows(JwtAuthenticationException.class, () -> jwtProvider.validateToken("token"));
    }

    @Test
    void validateTokenShouldThrowWhenTokenIsEmpty() {

        assertThrows(JwtAuthenticationException.class, () -> jwtProvider.validateToken(""));
    }

    @Test
    void getUsernameShouldReturnUserFromToken() {
        String token = jwtProvider.generateToken(user.getId(), user.getUsername());

        assertEquals(user.getUsername(), jwtProvider.getUsername(token));
    }
}
