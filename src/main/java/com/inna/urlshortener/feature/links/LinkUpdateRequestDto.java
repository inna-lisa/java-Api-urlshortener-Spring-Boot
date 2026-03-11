package com.inna.urlshortener.feature.links;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Data transfer object for updating a shortened link.
 * Request DTO for updating a shortened link.
 * Fields are optional — only provided fields will be updated.
 */
@Schema(description = "Request for updating a shortened link")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkUpdateRequestDto {

    @URL(message = "URL must start with http:// or https://")
    @Schema(
            description = "New original URL",
            example = "https://google.com",
            nullable = true)
    private String url;

    @Schema(
            description = "New expiration date",
            example = "2026-04-02T18:30:00",
            nullable = true)
    private LocalDateTime expiresAt;
}
