package com.example.Dating.service;

import com.example.Dating.dtos.request.LoginRequest;
import com.example.Dating.dtos.request.RefreshTokenRequest;
import com.example.Dating.dtos.request.RegisterRequest;
import com.example.Dating.dtos.response.AuthResponse;
import com.example.Dating.entities.User;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.UserEloScoreRepository;
import com.example.Dating.repository.UserRepository;
import com.example.Dating.security.JwtTokenProvider;
import com.example.Dating.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserEloScoreRepository eloScoreRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Password123!");

        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("validRefreshToken");

        user = User.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenProvider.generateAccessToken(any(), any())).thenReturn("access");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("refresh");

        AuthResponse response = authService.register(registerRequest);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getHasProfile()).isFalse();
        verify(eloScoreRepository).save(any());
    }

    @Test
    void register_DuplicateUsername_Throws() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void register_DuplicateEmail_Throws() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);
        when(tokenProvider.generateAccessToken(any(), any())).thenReturn("access");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("refresh");

        AuthResponse response = authService.login(loginRequest);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getAccessToken()).isEqualTo("access");
    }

    @Test
    void login_UserNotFound_Throws() {
        when(userRepository.findByUsernameOrEmail("testuser")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid username/email or password");
    }

    @Test
    void login_WrongPassword_Throws() {
        when(userRepository.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(false);
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewTokens() {
        when(tokenProvider.validateToken("validRefreshToken")).thenReturn(true);
        when(tokenProvider.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(tokenProvider.getUserIdFromToken("validRefreshToken")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenProvider.generateAccessToken(any(), any())).thenReturn("newAccess");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("newRefresh");

        AuthResponse response = authService.refreshToken(refreshTokenRequest);
        assertThat(response.getAccessToken()).isEqualTo("newAccess");
        assertThat(response.getRefreshToken()).isEqualTo("newRefresh");
    }

    @Test
    void refreshToken_InvalidToken_Throws() {
        when(tokenProvider.validateToken("validRefreshToken")).thenReturn(false);
        assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void findById_UserExists_ReturnsUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User found = authService.findById(userId);
        assertThat(found).isEqualTo(user);
    }

    @Test
    void findById_UserNotFound_Throws() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.findById(UUID.randomUUID()))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }
}