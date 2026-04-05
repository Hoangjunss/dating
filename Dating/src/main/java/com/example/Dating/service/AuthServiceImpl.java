package com.example.Dating.service;

import com.example.Dating.dtos.request.LoginRequest;
import com.example.Dating.dtos.request.RefreshTokenRequest;
import com.example.Dating.dtos.request.RegisterRequest;
import com.example.Dating.dtos.response.AuthResponse;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserEloScore;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.UserEloScoreRepository;
import com.example.Dating.repository.UserRepository;
import com.example.Dating.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserEloScoreRepository eloScoreRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user - username: {}, email: {}", request.getUsername(), request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        //seed Elo score mặc định ngay khi register
        UserEloScore elo = UserEloScore.builder()
                .userId(savedUser.getUserId())
                .score(1400.0)
                .totalSeen(0L)
                .totalLikes(0L)
                .build();
        eloScoreRepository.save(elo);

        log.info("User registered with userId: {}", savedUser.getUserId());

        String accessToken  = tokenProvider.generateAccessToken(savedUser.getUserId(), savedUser.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getUserId());

        return AuthResponse.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .hasProfile(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Login — accepts username or email in the 'identifier' field
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getUsername().trim();
        log.info("Login attempt - identifier: {}", identifier);

        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> {
                    log.warn("Login failed — not found: {}", identifier);
                    return new ValidationException("Invalid username/email or password");
                });

        log.info("User attempt - identifier: {}, {}, {}", user.getEmail(), user.getPassword(), request.getPassword());

        // Plain text compare — đổi thành BCrypt khi add security
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed — wrong password for identifier: {}", identifier);
            throw new ValidationException("Invalid username/email or password");
        }

        log.info("Login successful. userId: {}", user.getUserId());
        String accessToken  = tokenProvider.generateAccessToken(user.getUserId(), user.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        return AuthResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .hasProfile(user.getProfile() != null)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!tokenProvider.validateToken(token) || !tokenProvider.isRefreshToken(token)) {
            throw new ValidationException("Invalid or expired refresh token");
        }

        UUID userId = tokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        String newAccessToken  = tokenProvider.generateAccessToken(user.getUserId(), user.getUsername());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        log.info("Token refreshed for userId: {}", userId);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .hasProfile(user.getProfile() != null)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User not found"));
    }
}