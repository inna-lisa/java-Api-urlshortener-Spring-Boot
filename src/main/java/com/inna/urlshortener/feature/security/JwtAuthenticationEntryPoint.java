package com.inna.urlshortener.feature.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Entry point for handling unauthorized access attempts.
 * This component is triggered when an unauthenticated user
 * tries to access a secured REST endpoint.
 * It returns a JSON response with HTTP status 401 (Unauthorized)
 * containing error details.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * This method is called when authentication fails.
     * It builds a JSON response with error details and writes it to the response body.
     *
     * @param request       the HTTP request that resulted in an {@link AuthenticationException}
     * @param response      the HTTP response
     * @param authException the exception that caused the invocation
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Object error = request.getAttribute("jwt-exception");

        String message = error != null ? error.toString() : "Token is missing";

        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getRequestURI());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}

