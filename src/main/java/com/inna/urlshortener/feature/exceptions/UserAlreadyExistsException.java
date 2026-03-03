package com.inna.urlshortener.feature.exceptions;

/**
 * Exception thrown when user registration fails
 * because the username already exists.
 */

public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message detailed error message
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
