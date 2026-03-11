package com.inna.urlshortener.feature.security;

import com.inna.urlshortener.feature.exceptions.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter.
 * Extracts JWT token from Authorization header
 * Validates token using {@link JwtProvider}
 * Sets authentication in {@link SecurityContextHolder}
 * If token is invalid or missing, request continues without authentication.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * Processes incoming request and performs JWT authentication.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param filterChain filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        SecurityContextHolder.clearContext();

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                jwtProvider.validateToken(token);

                String username = jwtProvider.getUsername(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtAuthenticationException ex) {
                SecurityContextHolder.clearContext();
                request.setAttribute("jwt-exception", ex.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Determines whether this filter should be applied.
     *
     * <p>
     * Skips filtering for Swagger and OpenAPI documentation endpoints.
     *
     * @param request incoming request
     * @return {@code true} if filtering should be skipped, {@code false} otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();
        return path.startsWith("/swagger-ui.html")
                || path.startsWith("/swagger-ui/**")
                || path.startsWith("/v3/api-docs/**")
                || path.startsWith("/webjars/**")
                || path.startsWith("/swagger-resources/**");
    }
}
