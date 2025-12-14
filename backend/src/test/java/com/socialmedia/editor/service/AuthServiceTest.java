package com.socialmedia.editor.service;

import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        ReflectionTestUtils.setField(authService, "jwtSecret", "testSecretKeyForTestingThatIs32CharactersLong!");
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 86400000);
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnToken() {
        String username = "testuser";
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, "test@example.com", encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String token = authService.authenticateUser(username, password);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        verify(userRepository).findByUsername(username);
    }

    @Test
    void authenticateUser_WithInvalidUsername_ShouldReturnNull() {
        String username = "nonexistentuser";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        String token = authService.authenticateUser(username, password);

        assertThat(token).isNull();
        verify(userRepository).findByUsername(username);
    }

    @Test
    void authenticateUser_WithInvalidPassword_ShouldReturnNull() {
        String username = "testuser";
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String encodedPassword = passwordEncoder.encode(correctPassword);
        User user = new User(username, "test@example.com", encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String token = authService.authenticateUser(username, wrongPassword);

        assertThat(token).isNull();
        verify(userRepository).findByUsername(username);
    }

    @Test
    void registerUser_WithValidData_ShouldReturnUser() {
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });

        User result = authService.registerUser(username, email, password);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldThrowException() {
        String username = "existinguser";
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(username, email, password))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username is already taken!");

        verify(userRepository).existsByUsername(username);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        String username = "newuser";
        String email = "existing@example.com";
        String password = "password123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(username, email, password))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email is already in use!");

        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void generateJwtToken_ShouldReturnValidToken() {
        String username = "testuser";

        String token = authService.generateJwtToken(username);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts separated by dots
    }

    @Test
    void getUsernameFromJwtToken_ShouldReturnCorrectUsername() {
        String username = "testuser";
        String token = authService.generateJwtToken(username);

        String extractedUsername = authService.getUsernameFromJwtToken(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void validateJwtToken_WithValidToken_ShouldReturnTrue() {
        String username = "testuser";
        String token = authService.generateJwtToken(username);

        boolean isValid = authService.validateJwtToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateJwtToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.jwt.token";

        boolean isValid = authService.validateJwtToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void validateJwtToken_WithEmptyToken_ShouldReturnFalse() {
        boolean isValid = authService.validateJwtToken("");

        assertThat(isValid).isFalse();
    }

    @Test
    void validateJwtToken_WithNullToken_ShouldReturnFalse() {
        boolean isValid = authService.validateJwtToken(null);

        assertThat(isValid).isFalse();
    }
}