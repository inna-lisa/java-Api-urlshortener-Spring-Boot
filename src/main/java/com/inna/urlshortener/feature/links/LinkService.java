package com.inna.urlshortener.feature.links;

import java.util.List;

/**
 * Service interface for link management operations.
 * Provides CRUD methods for links create, find,
 * and delete of li link information.
 */
public interface LinkService {

    /**
     * Create new short link.
     *
     * @param requestDto original link.
     * @param username authorized user.
     * @return response DTO.
     */
    LinkResponseDto create(LinkRequestDto requestDto, String username);

    /**
     * Get original URL by short link.
     *
     * @param shortLink short URL.
     * @return original URL.
     */
    String getLink(String shortLink);

    /**
     * Delete link.
     *
     * @param shortLink short URL.
     * @param username authorized user.
     */
    void delete(String shortLink, String username);

    /**
     * Get all user's links.
     *
     * @param username authorized user.
     * @return list of links.
     */
    List<LinkResponseDto> getUserLinks(String username);

    /**
     * Get active user's links.
     *
     * @param username authorized user.
     * @return list of links.
     */
    List<LinkResponseDto> getActiveUserLinks(String username);
}
