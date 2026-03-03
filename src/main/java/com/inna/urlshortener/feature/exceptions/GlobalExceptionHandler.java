package com.inna.urlshortener.feature.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Global exception handler for the application.
 * Provides centralized handling of application exceptions and returns
 * appropriate error responses to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR = "error";

    /**
     * Handles runtime exceptions.
     *
     * @param ex the thrown runtime exception
     * @return response entity with error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles bad requests.
     *
     * @param ex the thrown validation exception
     * @return response entity with validation error messages
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR, ex.getMessage());
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(error);
    }

    /**
     * Handles security exceptions.
     *
     * @param ex the thrown validation exception
     * @return response entity with validation error messages
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(SecurityException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR, ex.getMessage());
        return ResponseEntity.status((HttpStatus.FORBIDDEN)).body(error);
    }

    /**
     * Handles user already exists exception.
     *
     * @param ex thrown exception
     * @return CONFLICT response with error message
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleNotExistsException(UserAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR, ex.getMessage());
        return ResponseEntity.status((HttpStatus.CONFLICT)).body(error);
    }

    /**
     * Handles user not found exception.
     *
     * @param ex thrown exception
     * @return NOT_FOUND response with error message
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR, ex.getMessage());
        return ResponseEntity.status((HttpStatus.NOT_FOUND)).body(error);
    }

    /**
     * Handles invalid credentials exception.
     *
     * @param ex thrown exception
     * @return BAD_REQUEST response with error message
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR, ex.getMessage());
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(error);
    }

    /**
     * Handles link not found exception.
     *
     * @param ex thrown exception
     * @return NOT_FOUND response with error message
     */
    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<String> handleNotFound(LinkNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles expired link exception.
     *
     * @param ex thrown exception
     * @return GONE response with error message
     */
    @ExceptionHandler(LinkExpiredException.class)
    public ResponseEntity<String> handleNotFound(LinkExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
    }
}
