package com.example.Dating.service;

import com.example.Dating.dtos.request.LoginRequest;
import com.example.Dating.dtos.request.RegisterRequest;
import com.example.Dating.dtos.response.AuthResponse;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserEloScore;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.UserEloScoreRepository;
import com.example.Dating.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                .password(request.getPassword()) // NOTE: nên hash password ở đây
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

        return AuthResponse.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
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
                    return new ResourceNotFoundException("Invalid username/email or password");
                });

        log.info("User attempt - identifier: {}, {}, {}", user.getEmail(), user.getPassword(), request.getPassword());

        // Plain text compare — đổi thành BCrypt khi add security
        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Login failed — wrong password for identifier: {}", identifier);
            throw new ValidationException("Invalid username/email or password");
        }

        log.info("Login successful. userId: {}", user.getUserId());
        return buildResponse(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User not found"));
    }

    private AuthResponse buildResponse(User user) {
        return AuthResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                // hasProfile = true nếu đã tạo profile (bước 2), false nếu chưa
                .hasProfile(user.getProfile() != null)
                .build();
    }
}