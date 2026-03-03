package com.inna.urlshortener.feature.exceptions;

/**
 * Exception thrown when link not found
 * due to invalid credentials.
 */
public class LinkNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message detailed error message
     */
    public LinkNotFoundException(String message) {
        super(message);
    }
}
