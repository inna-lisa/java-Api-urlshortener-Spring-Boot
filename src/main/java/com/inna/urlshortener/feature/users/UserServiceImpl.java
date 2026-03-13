package com.inna.urlshortener.feature.users;

import com.inna.urlshortener.feature.exceptions.InvalidCredentialsException;
import com.inna.urlshortener.feature.exceptions.UserAlreadyExistsException;
import com.inna.urlshortener.feature.exceptions.UserNotFoundException;
import com.inna.urlshortener.feature.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserService}.
 * Provides user management operations including registration and authentication.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto registration(UserRequestDto userRequestDto) {
        if (userIsExist(userRequestDto.getUsername())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        validatePassword(userRequestDto.getPassword());

        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        userRepository.save(user);

        return userMapper.toDto(user, null);
    }

    @Override
    public UserResponseDto authorization(UserRequestDto userRequestDto) {

        User user = userRepository.findByUsername(userRequestDto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String token = jwtProvider.generateToken(user.getId(), user.getUsername());

        return userMapper.toDto(user, token);
    }

    /**
     * Check if username is in the DB.
     */
    private boolean userIsExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Validate password.
     */
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new InvalidCredentialsException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*\\d.*")) {
            throw new InvalidCredentialsException("Password must contain at least one digit");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new InvalidCredentialsException("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidCredentialsException("Password must contain at least one uppercase letter");
        }
    }
}
