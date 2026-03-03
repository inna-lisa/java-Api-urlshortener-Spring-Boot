package com.inna.urlshortener.feature.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user management operations.
 * Provides endpoints for:
 * user registration
 * user authentication
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations with user accounts")
public class UserRestController {

    private final UserService userService;

    /**
     * Registers a new user.
     *
     * @param userRequestDto user data transfer object containing registration information
     * @return response entity with registered user details
     */
    @Operation(summary = "User registration",
            description = "Creates new user account and returns authentication token")
    @ApiResponse(responseCode = "201",
            description = "User successfully registered",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping("/registration")
    public ResponseEntity<UserResponseDto> registration(@RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.registration(userRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    /**
     * Authenticates a user with provided credentials.
     *
     * @param userRequestDto user data transfer object containing login credentials
     * @return response entity with authenticated user details
     */
    @Operation(summary = "User authentication",
            description = "Authenticates user and returns JWT token")
    @ApiResponse(responseCode = "200",
            description = "User successfully authenticated",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
    @PostMapping("/authorization")
    public ResponseEntity<UserResponseDto> authorization(@RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.authorization(userRequestDto);

        return ResponseEntity.ok(userResponseDto);
    }
}
