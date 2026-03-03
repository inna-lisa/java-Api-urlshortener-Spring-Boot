package com.inna.urlshortener.feature.links;

import com.inna.urlshortener.feature.exceptions.LinkExpiredException;
import com.inna.urlshortener.feature.exceptions.LinkNotFoundException;
import com.inna.urlshortener.feature.exceptions.UserNotFoundException;
import com.inna.urlshortener.feature.users.User;
import com.inna.urlshortener.feature.users.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of {@link LinkService}.
 * Provides business logic for:
 * Link creation
 * Link redirection
 * Statistics update
 * Link deletion
 */

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {

    private static final int SHORT_URL_LENGTH = 8;
    private static final int EXPIRATION_PERIOD = 30;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    /**
     * Creates new short link.
     *
     * @param requestDto request with original URL
     * @param username owner username (not null validation on {@link LinkRestController})
     * @return created link DTO
     * @throws UserNotFoundException if user not found
     */
    @Override
    public LinkResponseDto create(LinkRequestDto requestDto, String username) {

        Link link = new Link();
        link.setShortLink(generationUniqueShortUrl());
        link.setUrl(requestDto.getUrl());
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(EXPIRATION_PERIOD));
        link.setOpenCount(0);
        link.setUser(userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new));

        return toDto(linkRepository.save(link));
    }

    /**
     * Returns original URL by short link and updates statistics.
     *
     * @param shortLink short URL (not null validation on {@link LinkRestController})
     * @return original URL
     * @throws LinkNotFoundException if link not found or expired
     */
    @Override
    @Transactional
    public String getLink(String shortLink) {

        Link link = linkRepository.findByShortLink(shortLink)
                .orElseThrow(() -> new LinkNotFoundException("Short URL doesn't exist"));
        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new LinkExpiredException("Link expired");
        }
        link.setOpenCount(link.getOpenCount() + 1);
        return link.getUrl();
    }

    /**
     * Deletes link if user is owner.
     *
     * @param shortLink short URL (not null validation on {@link LinkRestController})
     * @param username owner username (not null validation on {@link LinkRestController})
     * @throws IllegalArgumentException if user is not owner
     */
    @Override
    @Transactional
    public void delete(String shortLink, String username) {
        Link link = linkRepository.findByShortLink(shortLink)
                .orElseThrow(() -> new IllegalArgumentException("Short URL doesn't exist"));
        if (!link.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not the owner of this link");
        }
        linkRepository.delete(link);
    }

    /**
     * Returns all links of user.
     *
     * @param username owner username (not null validation on {@link LinkRestController})
     * @return list of link DTOs
     */
    @Override
    public List<LinkResponseDto> getUserLinks(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        List<Link> linksByUser = linkRepository.findByUser(user);

        return linksByUser.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Returns all active links of user.
     *
     * @param username owner username (not null validation on {@link LinkRestController})
     * @return list of link DTOs
     */
    @Override
    public List<LinkResponseDto> getActiveUserLinks(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        List<Link> linksByUser = linkRepository.findByUserAndExpiresAtAfter(user, LocalDateTime.now());

        return linksByUser.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Generates unique short URL.
     * Method ensures generated code does not already exist in database.
     *
     * @return unique short URL
     */
    private String generationUniqueShortUrl() {
        String shortUrl;
        do {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < SHORT_URL_LENGTH; i++) {
                stringBuilder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            shortUrl = stringBuilder.toString();
        } while (linkRepository.existsById(shortUrl));

        return shortUrl;
    }

    /**
     * Converts Link entity to response DTO.
     *
     * @param link link entity
     * @return response DTO with link information
     */
    private LinkResponseDto toDto(Link link) {

        return new LinkResponseDto(
                link.getShortLink(),
                link.getUrl(),
                link.getCreatedAt(),
                link.getExpiresAt(),
                link.getOpenCount(),
                link.getUser().getUsername());
    }
}
