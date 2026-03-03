package com.inna.urlshortener.feature.exceptions;

/**
 * Exception thrown when user not found.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     */
    public UserNotFoundException() {
        super("User not found");
    }
}
