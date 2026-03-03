package com.inna.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the application.
 * Bootstraps the Spring Boot application.
 */
@SpringBootApplication
public class UrlShortenerApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);
    }

}
