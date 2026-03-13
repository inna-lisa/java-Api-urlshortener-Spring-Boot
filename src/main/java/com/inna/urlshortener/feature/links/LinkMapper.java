package com.inna.urlshortener.feature.links;

import com.inna.urlshortener.feature.users.User;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Mapper for Link entity and related DTOs.
 */
@Component
public class LinkMapper {

    @Value("${link.expiration-days}")
    private int expirationPeriod;

    /**
     * Converts Link entity to response DTO.
     *
     * @param link link entity
     * @return response DTO with link information
     */
    public LinkResponseDto toDto(Link link) {

        if (link == null) {
            return null;
        }
        return new LinkResponseDto(
                link.getShortLink(),
                link.getUrl(),
                link.getCreatedAt(),
                link.getExpiresAt(),
                link.getOpenCount(),
                link.getUser().getUsername());
    }

    /**
     * Converts create request DTO to Link entity.
     *
     * @param shortUrl short link
     * @param requestDto request with original URL
     * @param user owner user
     * @return link entity
     */
    public Link toEntity(String shortUrl, LinkCreateRequestDto requestDto, User user) {

        Link link = new Link();
        link.setShortLink(shortUrl);
        link.setUrl(requestDto.getUrl());
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(expirationPeriod));
        link.setOpenCount(0);
        link.setUser(user);

        return link;
    }
}
