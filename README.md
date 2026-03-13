[![CI](https://github.com/inna-lisa/java-Api-urlshortener-Spring-Boot/actions/workflows/ci.yml/badge.svg)](https://github.com/inna-lisa/java-Api-urlshortener-Spring-Boot/actions)
![Coverage](https://img.shields.io/badge/coverage-94%25-brightgreen)
# URL Shortener API

REST API service for shortening URLs with user authentication and link statistics.

The application allows users to:

- register and authenticate
- create short URLs
- redirect to original URLs
- manage their own links
- view link statistics


---

## Tech Stack

- Java 21
- Spring Boot (Web MVC, Security, Validation, Data JPA)
- PostgreSQL
- Flyway (database migrations)
- JWT authentication (jjwt)
- Springdoc OpenAPI / Swagger UI
- Lombok
- Gradle
- Docker Compose
- JUnit 5
- Spring Boot Test
- Testcontainers (integration testing)

---

## Environment variables

Before running the application, you must set the following environment variables:

### Required variables

| Variable             | Description                                |
|----------------------|--------------------------------------------|
| DB_USERNAME          | Database username                          |
| DB_PASSWORD          | Database password                          |
| DB_URL               | JDBC connection URL                        |
| JWT_SECRET           | Secret key used for signing JWT tokens     |
| JWT_EXPIRATION       | JWT token expiration time in milliseconds  |
| LINK_EXPIRATION_DAYS | Number of days before a short link expires |

#### Example (Linux/macOS):

bash

`export DB_URL=jdbc:postgresql://localhost:5432/url_shortener`

`export DB_USERNAME=username`

`export DB_PASSWORD=UserPassword123`

`export JWT_SECRET=very-long-and-secure-key-32-plus-chars-and-secure-key-32-plus-chars`

`export JWT_EXPIRATION=3600000`

`export LINK_EXPIRATION_DAYS=30`

#### Example (Windows PowerShell):

PowerShell

`$env:DB_URL="jdbc:postgresql://localhost:5432/urlshortener"`

`$env:DB_USERNAME="username"`

`$env:DB_PASSWORD="UserPassword123"`

`$env:JWT_SECRET="very-long-and-secure-key-32-plus-chars-and-secure-key-32-plus-chars"`

`$env:JWT_EXPIRATION="3600000"`

`$env:LINK_EXPIRATION_DAYS="30"`

---

## Running the Application Locally

1. Start PostgreSQL (locally or via Docker)

2. Set required environment variables

3. Run the application:

./gradlew bootRun

The application will start on:

http://localhost:8080

---

## Running with Docker Compose

The project includes a "compose.yaml" file for running PostgreSQL.

Start containers:

docker compose up -d

Stop containers:

docker compose down

After starting PostgreSQL, run the application:

./gradlew bootRun

---

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

### Run all tests:

./gradlew test

Integration tests use Testcontainers, which automatically starts a PostgreSQL container.

Documentation: https://java.testcontainers.org/

### Test Coverage

Coverage is measured using JaCoCo.

Generate coverage report:

./gradlew test jacocoTestReport

Coverage report will be available at:

build/reports/jacoco/test/html/index.html

---

## Main API Endpoints

##### Base path:

/api/v1

#### Authentication

| Method | Endpoint                    | Description                       |
|--------|-----------------------------|-----------------------------------|
| POST   | /api/v1/users/registration  | Register new user                 |
| POST   | /api/v1/users/authorization | Authenticate user and receive JWT |

#### Links

| Method | Endpoint                  | Description           |
|--------|---------------------------|-----------------------|
| POST   | /api/v1/links             | Create short URL      |
| GET    | /api/v1/links             | Get all user links    |
| GET    | /api/v1/links/active      | Get active user links |
| PATCH  | /api/v1/links/{shortLink} | Update link           |
| DELETE | /api/v1/links/{shortLink} | Delete link           |

#### Redirect

| Method | Endpoint                  | Description                       |
|--------|---------------------------|-----------------------------------|
| GET    | /api/v1/links/{shortLink} | Redirect to original URL          |
