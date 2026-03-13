package com.inna.urlshortener.feature.links;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Data transfer object for creating a shortened link.
 * Contains original URL that should be shortened.
 */
@Schema(description = "Request for creating a shortened link")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkCreateRequestDto {
    @NotBlank(message = "URL must not be empty")
    @URL(message = "URL must start with http:// or https://")
    @Schema(
            description = "Original URL that should be shortened",
            example = "https://google.com")
    private String url;
}
