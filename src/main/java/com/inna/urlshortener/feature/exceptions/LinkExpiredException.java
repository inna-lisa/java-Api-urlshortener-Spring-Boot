package com.inna.urlshortener.feature.exceptions;

/**
 * Exception thrown when link is expired
 * due to invalid credentials.
 */
public class LinkExpiredException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     *
     * @param message detailed error message
     */
    public LinkExpiredException(String message) {

        super(message);
    }
}
