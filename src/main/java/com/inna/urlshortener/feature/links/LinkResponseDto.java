package com.inna.urlshortener.feature.links;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for link response.
 * Contains information about the shortened link returned to the client.
 */
@Schema(description = "Response containing shortened link information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkResponseDto {

    @Schema(
            description = "Generated short URL",
            example = "http://localhost:8080/api/links/abc123"
    )
    private String shortUrl;

    @Schema(
            description = "Original URL",
            example = "https://google.com"
    )
    private String url;

    @Schema(
            description = "Date and time when the link was created",
            example = "2026-03-02T18:30:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Date and time when the link expires",
            example = "2026-04-02T18:30:00"
    )
    private LocalDateTime expiresAt;

    @Schema(
            description = "Number of times the link was opened",
            example = "15"
    )
    private int openCount = 0;

    @Schema(
            description = "Username of the link owner",
            example = "testUser"
    )
    private String username;
}
