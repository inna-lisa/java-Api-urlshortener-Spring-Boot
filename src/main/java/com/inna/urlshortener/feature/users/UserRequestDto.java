package com.inna.urlshortener.feature.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request data transfer object for user authentication or registration.
 * Contains credentials provided by the client.
 */
@Schema(description = "Request for user authentication or registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank
    @Schema(
            description = "Username for authentication",
            example = "testUser"
    )
    private String username;

    @NotBlank
    @Schema(
            description = "User password",
            example = "StrongPass123"
    )
    private String password;
}
