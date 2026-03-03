[![CI](https://github.com/inna-lisa/java-Api-urlshortener-Spring-Boot/actions/workflows/ci.yml/badge.svg)](https://github.com/inna-lisa/java-Api-urlshortener-Spring-Boot/actions)
![Coverage](https://img.shields.io/badge/coverage-95%25-brightgreen)
# URL Shortener API

Simple URL shortening service with user authentication and link management.

---

## Tech Stack

Java 21
Spring Boot (Web MVC, Security, Validation, Data JPA, JDBC)
PostgreSQL
Flyway (database migrations)
JWT (jjwt)
Springdoc OpenAPI / Swagger UI
Lombok
Gradle
Docker Compose (local PostgreSQL)
JUnit 5 + Spring Boot Test + Testcontainers (integration tests)

---

## Environment variables

Before running the application, you must set environment variables for database access.

### Required variables

| Variable         | Description          |
|------------------|----------------------|
| DB_USERNAME      | Database username    |
| DB_PASSWORD      | Database password    |
| DB_URL           | JDBC connection URL  |

Example (Linux/macOS):

bash

`export DB_USERNAME=your_user`

`export DB_PASSWORD=your_password`

`export DB_URL=jdbc:postgresql://localhost:5432/urlshortener`


Example (Windows PowerShell):

PowerShell

`$env:DB_USERNAME="your_user"`

`$env:DB_PASSWORD="your_password"`

`$env:DB_URL="jdbc:postgresql://localhost:5432/urlshortener"`

## API Documentation (Swagger/OpenAPI)

The project uses Swagger/OpenAPI for API documentation.

After running the application, documentation is available at:

http://localhost:8080/swagger-ui/index.html

Swagger allows you to:
- View all API endpoints
- Test requests directly in browser
- See request and response schemas
- Explore OpenAPI specification

---

## Tests

Integration tests use Testcontainers: https://java.testcontainers.org/