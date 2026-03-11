package com.inna.urlshortener.feature.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inna.urlshortener.feature.exceptions.LinkExpiredException;
import com.inna.urlshortener.feature.exceptions.LinkNotFoundException;
import com.inna.urlshortener.feature.exceptions.UserNotFoundException;
import com.inna.urlshortener.feature.users.User;
import com.inna.urlshortener.feature.users.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkServiceImplTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LinkServiceImpl linkService;

    private User user;
    private Link link;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");

        link = new Link();
        link.setShortLink("exa");
        link.setUrl("http://example.com");
        link.setOpenCount(0);
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(30));
        link.setUser(user);
    }

    @Test
    void createShouldCreateShortLink() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        LinkRequestDto linkRequestDto = new LinkRequestDto(link.getUrl());
        LinkResponseDto linkResponseDto = linkService.create(linkRequestDto, user.getUsername());

        assertNotNull(linkResponseDto);
        assertEquals(linkRequestDto.getUrl(), linkResponseDto.getUrl());

        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void createShouldTrowUserNotFound() {

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        LinkRequestDto linkRequestDto = new LinkRequestDto(link.getUrl());
        assertThrows(UserNotFoundException.class,
                () -> linkService.create(linkRequestDto, "unknown"));
    }

    @Test
    void getLinkShouldReturnOriginalUrlAndIncreaseOpenCount() {
        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.of(link));

        String originalLink = linkService.getLink(link.getShortLink());

        assertEquals(link.getUrl(), originalLink);
        assertEquals(1, link.getOpenCount());
    }

    @Test
    void getLinkShouldThrowIfNotFound() {
        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.empty());

        String shortLink = link.getShortLink();
        assertThrows(LinkNotFoundException.class, () -> linkService.getLink(shortLink));
    }

    @Test
    void getLinkShouldThrowIfExpired() {
        link.setExpiresAt(LocalDateTime.now().minusDays(1));
        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.of(link));

        String shortLink = link.getShortLink();
        assertThrows(LinkExpiredException.class, () -> linkService.getLink(shortLink));
    }

    @Test
    void deleteShouldRemoveLinkIfOwner() {
        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.of(link));

        linkService.delete(link.getShortLink(), user.getUsername());

        verify(linkRepository,times(1)).delete(any(Link.class));
    }

    @Test
    void deleteThrowExceptionIfNotOwner() {
        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.of(link));

        String shortLink = link.getShortLink();
        assertThrows(SecurityException.class, () -> linkService.delete(shortLink, "unknown"));
    }

    @Test
    void deleteThrowExceptionIfLinkNotFound() {
        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.empty());

        String shortLink = link.getShortLink();
        String username = user.getUsername();
        assertThrows(IllegalArgumentException.class, () -> linkService.delete(shortLink, username));
    }

    @Test
    void getUserLinksShouldReturnLinksByUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(linkRepository.findByUser(user)).thenReturn(List.of(link));

        List<LinkResponseDto> userLinks = linkService.getUserLinks(user.getUsername());

        assertEquals(1, userLinks.size());
        assertEquals(link.getShortLink(), userLinks.getFirst().getShortUrl());
    }

    @Test
    void getUserLinksShouldTrowExceptionIfUserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        String username = user.getUsername();
        assertThrows(UserNotFoundException.class, () -> linkService.getUserLinks(username));
    }

    @Test
    void getActiveUserLinksShouldReturnActiveLinksByUser() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(linkRepository.findByUserAndExpiresAtAfter(eq(user), any(LocalDateTime.class))).thenReturn(List.of(link));

        List<LinkResponseDto> userLinks = linkService.getActiveUserLinks(user.getUsername());

        assertEquals(1, userLinks.size());
        assertEquals(link.getShortLink(), userLinks.getFirst().getShortUrl());
    }

    @Test
    void getActiveUserLinksShouldTrowExceptionIfUserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        String username = user.getUsername();
        assertThrows(UserNotFoundException.class, () -> linkService.getActiveUserLinks(username));
    }

    @Test
    void updateShouldUpdateUrlWhenOwner() {
        LinkUpdateRequestDto linkUpdateRequestDto = new LinkUpdateRequestDto();
        linkUpdateRequestDto.setUrl("https://new.com");
        linkUpdateRequestDto.setExpiresAt(LocalDateTime.now().plusDays(60));

        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.of(link));

        LinkResponseDto update = linkService.update(link.getShortLink(), linkUpdateRequestDto, link.getUser().getUsername());

        assertEquals(linkUpdateRequestDto.getUrl(), link.getUrl());
        assertEquals(linkUpdateRequestDto.getExpiresAt(), link.getExpiresAt());

        verify(linkRepository).findByShortLink(link.getShortLink());
    }

    @Test
    void updateShouldTrowExceptionWhenNotOwner() {
        LinkUpdateRequestDto linkUpdateRequestDto = new LinkUpdateRequestDto();
        linkUpdateRequestDto.setUrl("https://new.com");

        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.of(link));

        assertThrows(SecurityException.class,
               () -> linkService.update(link.getShortLink(), linkUpdateRequestDto, "notOwner"));
    }

    @Test
    void updateShouldTrowExceptionWhenLinkNotFound() {
        LinkUpdateRequestDto linkUpdateRequestDto = new LinkUpdateRequestDto();
        linkUpdateRequestDto.setUrl("https://new.com");

        when(linkRepository.findByShortLink(link.getShortLink())).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class,
                () -> linkService.update(link.getShortLink(), linkUpdateRequestDto, link.getUser().getUsername()));
    }
}
