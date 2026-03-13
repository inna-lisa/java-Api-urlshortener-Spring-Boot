package com.inna.urlshortener.feature.links;

import com.inna.urlshortener.feature.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LinkMapperTest {

    private LinkMapper linkMapper;

    private User user;
    private LinkCreateRequestDto createDto;
    private Link link;

    @BeforeEach
    void setUp() {
        linkMapper = new LinkMapper();

        user = new User();
        user.setUsername("testUser");

        createDto = new LinkCreateRequestDto();
        createDto.setUrl("https://example.com");

        link = new Link();
        link.setShortLink("abc123");
        link.setUrl("https://example.com");
        link.setCreatedAt(LocalDateTime.of(2026, 3, 12, 14, 0));
        link.setExpiresAt(LocalDateTime.of(2026, 4, 12, 14, 0));
        link.setOpenCount(5);
        link.setUser(user);
    }

    @Test
    void toEntityCreatesEntityCorrectly() {
        Link entity = linkMapper.toEntity("abc123", createDto, user);

        assertThat(entity).isNotNull();
        assertThat(entity.getShortLink()).isEqualTo("abc123");
        assertThat(entity.getUrl()).isEqualTo("https://example.com");
        assertThat(entity.getUser()).isEqualTo(user);
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getExpiresAt()).isNotNull();
        assertThat(entity.getOpenCount()).isZero();
    }

    @Test
    void toDtoMapsEntityToDtoCorrectly() {
        LinkResponseDto dto = linkMapper.toDto(link);

        assertThat(dto).isNotNull();
        assertThat(dto.getShortUrl()).isEqualTo("abc123");
        assertThat(dto.getUrl()).isEqualTo("https://example.com");
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 3, 12, 14, 0));
        assertThat(dto.getExpiresAt()).isEqualTo(LocalDateTime.of(2026, 4, 12, 14, 0));
        assertThat(dto.getOpenCount()).isEqualTo(5);
        assertThat(dto.getUsername()).isEqualTo("testUser");
    }

    @Test
    void toDtoReturnsNullWhenEntityIsNull() {
        assertThat(linkMapper.toDto(null)).isNull();
    }
}