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
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final LinkMapper linkMapper;
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
    public LinkResponseDto create(LinkCreateRequestDto requestDto, String username) {

        Link link = linkMapper.toEntity(generationUniqueShortUrl(), requestDto,
                userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new));

        return linkMapper.toDto(linkRepository.save(link));
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
     * Updates an existing short link.
     *
     * @param shortLink short URL (not null validation on {@link LinkRestController})
     * @param requestDto request can contain new original URL
     * @param username owner username (not null validation on {@link LinkRestController})
     * @return update link DTO
     * @throws LinkNotFoundException if link not found
     */
    @Override
    @Transactional
    public LinkResponseDto update(String shortLink, LinkUpdateRequestDto requestDto, String username) {

        Link link = linkRepository.findByShortLink(shortLink)
                .orElseThrow(() -> new LinkNotFoundException("Short URL doesn't exist"));

        if (!link.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not the owner of this link");
        }

        if (requestDto.getUrl() != null) {
            link.setUrl(requestDto.getUrl());
        }

        if (requestDto.getExpiresAt() != null) {
            link.setExpiresAt(requestDto.getExpiresAt());
        }

        return linkMapper.toDto(link);
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
                .orElseThrow(() -> new LinkNotFoundException("Short URL doesn't exist"));
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
                .map(linkMapper::toDto)
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
                .map(linkMapper::toDto)
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
}
