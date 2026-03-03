package com.inna.urlshortener.feature.links;

import com.inna.urlshortener.feature.users.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Link entity.
 */
@Repository
public interface LinkRepository extends JpaRepository<Link, String> {

    /**
     * Find link by short URL.
     *
     * @param shortLink short code.
     * @return link if exists.
     */
    Optional<Link> findByShortLink(String shortLink);

    /**
     * Find all links by user.
     *
     * @param user is user.
     * @return links if exists.
     */
    List<Link> findByUser(User user);

    /**
     * Find active links after date by user.
     *
     * @param user is user.
     * @param now is data
     * @return links if exists.
     */
    List<Link> findByUserAndExpiresAtAfter(User user, LocalDateTime now);
}
