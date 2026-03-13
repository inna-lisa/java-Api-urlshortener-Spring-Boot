package com.inna.urlshortener.feature.security;

import com.inna.urlshortener.feature.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides JWT generation functionality.
 * Creates signed JSON Web Tokens used for authentication.
 */
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expiration;

    /**
     * Creates JwtProvider with configuration from application properties.
     *
     * @param secret JWT secret key (min 256-bit recommended)
     * @param expiration token lifetime in milliseconds
     */
    public JwtProvider(@Value("${security.jwt.secret}") String secret,
                       @Value("${security.jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    /**
     * Generates JWT token for user.
     *
     * @param userId user identifier
     * @param username user login name
     * @return signed JWT token
     */

    public String generateToken(long userId, String username) {

        return Jwts.builder()
                .subject(username)
                .claim("id", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * Validates JWT token signature and expiration.
     *
     * @param token JWT token.
     */
    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("Token is expired");
        } catch (MalformedJwtException ex) {
            throw new JwtAuthenticationException("Token is malformed");
        } catch (SignatureException ex) {
            throw new JwtAuthenticationException("Invalid token signature");
        } catch (UnsupportedJwtException ex) {
            throw new JwtAuthenticationException("Unsupported token");
        } catch (IllegalArgumentException ex) {
            throw new JwtAuthenticationException("Token is empty");
        }
    }

    /**
     * Extracts username from JWT token.
     *
     * @param token JWT token
     * @return username stored in token
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
