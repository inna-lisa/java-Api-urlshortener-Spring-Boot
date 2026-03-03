package com.inna.urlshortener.feature.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response data transfer object for user information.
 * Contains data returned to the client after authentication or registration.
 */
@Schema(description = "Response containing user information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    @Schema(description = "User identifier",
            example = "1")
    private Long id;

    @Schema(description = "Username",
            example = "testUser")
    private String username;

    @Schema(description = "JWT authentication token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;
}
