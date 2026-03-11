package com.inna.urlshortener.feature.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inna.urlshortener.feature.exceptions.InvalidCredentialsException;
import com.inna.urlshortener.feature.exceptions.UserAlreadyExistsException;
import com.inna.urlshortener.feature.exceptions.UserNotFoundException;
import com.inna.urlshortener.feature.security.JwtProvider;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto userRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto("testUser", "Password1");
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
    }

    @Test
    void registrationShouldCreateUserWhenUserIsNew() {
        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto userResponseDto = userService.registration(userRequestDto);

        assertEquals(user.getUsername(), userResponseDto.getUsername());
        assertNull(userResponseDto.getToken());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(userRequestDto.getPassword());
    }

    @Test
    void registrationShouldThrowExceptionWhenUserIsExist() {
        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registration(userRequestDto));

        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void registrationShouldThrowExceptionWhenPasswordInvalid(String password) {
        UserRequestDto userRequest = new UserRequestDto("testUser", password);

        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.registration(userRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void authorizationShouldAuthorizeWhenUserIsValid() {
        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.generateToken(user.getId(), user.getUsername())).thenReturn("mocked-token");

        UserResponseDto userResponseDto = userService.authorization(userRequestDto);

        assertEquals(user.getId(), userResponseDto.getId());
        assertEquals(user.getUsername(), userResponseDto.getUsername());
        assertEquals("mocked-token", userResponseDto.getToken());

        verify(passwordEncoder).matches(userRequestDto.getPassword(), user.getPassword());
    }

    @Test
    void authorizationShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.authorization(userRequestDto));

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authorizationShouldThrowExceptionWhenPasswordInvalid() {
        UserRequestDto userRequest = new UserRequestDto("testUser", "password1");

        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.authorization(userRequest));
    }

    static Stream<String> invalidPasswords() {

        return Stream.of("Pass1", "Password", "PASSWORD1", "password1");
    }
}
