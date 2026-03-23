package com.example.Dating.service;

import com.example.Dating.dtos.request.LoginRequest;
import com.example.Dating.dtos.request.RegisterRequest;
import com.example.Dating.dtos.response.AuthResponse;
import com.example.Dating.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
    User findById(UUID id);
}