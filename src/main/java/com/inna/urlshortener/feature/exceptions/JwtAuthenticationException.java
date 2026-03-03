package com.inna.urlshortener.feature.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when token authentication fails
 * due to invalid credentials.
 */
public class JwtAuthenticationException extends AuthenticationException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message detailed error message
     */
    public JwtAuthenticationException(String message) {
        super(message);
    }
}
