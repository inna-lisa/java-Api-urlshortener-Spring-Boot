package com.inna.urlshortener.feature.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for user persistence operations.
 * Provides methods for accessing and managing user data.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds user by username.
     *
     * @param username username to search
     * @return optional user if found
     */
    Optional<User> findByUsername(String username);
}
