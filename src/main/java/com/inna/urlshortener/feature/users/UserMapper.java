package com.inna.urlshortener.feature.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Mapper for User entity and related DTOs.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    /**
     * Converts User entity to response DTO.
     *
     * @param user User entity
     * @param token token
     * @return response DTO with link information
     */
    public UserResponseDto toDto(User user, String token) {

        return new UserResponseDto(user.getId(), user.getUsername(), token);
    }

    /**
     * Converts create request DTO to User entity.
     *
     * @param userRequestDto request
     * @return link entity
     */
    public User toEntity(UserRequestDto userRequestDto) {
        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        return user;
    }
}
