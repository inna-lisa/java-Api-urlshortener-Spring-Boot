package com.inna.urlshortener.feature.exceptions;

/**
 * Exception thrown when user authentication fails
 * due to invalid credentials.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message detailed error message
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
