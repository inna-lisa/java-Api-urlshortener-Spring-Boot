package com.inna.urlshortener.feature.users;

/**
 * Service interface for user management operations.
 * Provides methods for user registration, authentication,
 * and retrieval of user information.
 */
public interface UserService {

    /**
     * Registers a new user.
     *
     * @param userRequestDto user data transfer object containing registration information
     * @return response DTO with registered user information
     * @throws RuntimeException if username already exists or password is invalid
     */
    UserResponseDto registration(UserRequestDto userRequestDto);

    /**
     * Authenticates a user with provided credentials.
     *
     * @param userRequestDto user data transfer object containing login credentials
     * @return response DTO with authenticated user information
     * @throws RuntimeException if credentials are invalid
     */
    UserResponseDto authorization(UserRequestDto userRequestDto);
}
