package com.example.Dating.service;

import com.example.Dating.dtos.request.LoginRequest;
import com.example.Dating.dtos.request.RegisterRequest;
import com.example.Dating.dtos.response.AuthResponse;
import com.example.Dating.entities.User;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

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
                .password(request.getPassword())
                .build();

        userRepository.save(user);
        log.info("User registered successfully. userId: {}", user.getId());

        return buildResponse(user);
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

        // Plain text compare — đổi thành BCrypt khi add security
        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Login failed — wrong password for identifier: {}", identifier);
            throw new ValidationException("Invalid username/email or password");
        }

        log.info("Login successful. userId: {}", user.getId());
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // hasProfile = true nếu đã tạo profile (bước 2), false nếu chưa
                .hasProfile(user.getProfile() != null)
                .build();
    }
}