package com.inna.urlshortener.feature.links;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing shortened links.
 * Provides endpoints for:
 * - creating short links
 * - redirecting to original URLs
 * - retrieving user links
 * - deleting links
 */
@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
@Validated
@Tag(name = "Links", description = "Operations with shortened links")
public class LinkRestController {

    private final LinkService linkService;

    /**
     * Create new short URL.
     *
     * @param linkRequestDto link request DTO
     * @param principal authenticated user principal
     * @return response entity with created short URL DTO
     */
    @Operation(summary = "Create short link",
            description = "Creates a new shortened link for authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Short link successfully created",
                    content = @Content(schema = @Schema(implementation = LinkResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping()
    public ResponseEntity<LinkResponseDto> create(@RequestBody @Valid LinkRequestDto linkRequestDto,
                                 Principal principal) {
        LinkResponseDto linkResponseDto = linkService.create(linkRequestDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(linkResponseDto);
    }

    /**
     * Redirect to original URL.
     *
     * @param shortLink short URL
     * @return redirect response
     */
    @Operation(summary = "Redirect to original URL",
            description = "Redirects to the original URL using short link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
            @ApiResponse(responseCode = "404", description = "Link not found"),
            @ApiResponse(responseCode = "410", description = "Link expired")
    })
    @GetMapping("/{shortLink}")
    public ResponseEntity<Void> redirect(@Parameter(description = "Short link identifier", example = "abc123")
                                             @PathVariable("shortLink")
                                             @NotBlank(message = "Short link must not be blank")
                                             String shortLink) {

        String url = linkService.getLink(shortLink);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }

    /**
     * Update short URL.
     *
     * @param shortLink short URL
     * @param dto link update DTO
     * @param principal authenticated user principal.
     * @return updated link
     */
    @Operation(summary = "Update short link",
            description = "Change original URL ang/or expiration date of a short link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Link updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "User not found"),
            @ApiResponse(responseCode = "404", description = "Link not found"),
    })
    @PatchMapping("/{shortLink}")
    public ResponseEntity<LinkResponseDto> update(@Parameter(description = "Short link identifier", example = "abc123")
                                           @PathVariable("shortLink")
                                           @NotBlank(message = "Short link must not be blank")
                                           String shortLink,
                                                  @RequestBody @Valid LinkUpdateRequestDto dto, Principal principal) {

        LinkResponseDto linkResponseDto = linkService.update(shortLink, dto, principal.getName());

        return ResponseEntity.ok(linkResponseDto);
    }

    /**
     * Get all user links.
     *
     * @param principal authenticated user principal
     * @return response entity with list of short URL
     */
    @Operation(summary = "Get all user links",
            description = "Returns all shortened links created by authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of user links",
                    content = @Content(schema = @Schema(implementation = LinkResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<LinkResponseDto>> getUserLinks(Principal principal) {

        return ResponseEntity.ok(linkService.getUserLinks(principal.getName()));
    }

    /**
     * Get only active user links.
     *
     * @param principal authenticated user principal
     * @return response entity with list of active short URL
     */

    @Operation(summary = "Get active user links",
            description = "Returns only active (not expired) links of authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of active links",
                    content = @Content(schema = @Schema(implementation = LinkResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/active")
    public ResponseEntity<List<LinkResponseDto>> getActiveUserLinks(Principal principal) {

        return ResponseEntity.ok(linkService.getActiveUserLinks(principal.getName()));
    }

    /**
     * Delete user links.
     *
     * @param shortLink short URL
     * @param principal authenticated user principal
     * @return empty response with status 204 No content
     */
    @Operation(summary = "Delete link",
            description = "Deletes a specific short link owned by authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Link successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Link not found")
    })
    @DeleteMapping("/{shortLink}")
    public ResponseEntity<Void> delete(@Parameter(description = "Short link identifier", example = "abc123")
                                           @PathVariable("shortLink") String shortLink, Principal principal) {
        linkService.delete(shortLink, principal.getName());

        return ResponseEntity.noContent().build();
    }
}
