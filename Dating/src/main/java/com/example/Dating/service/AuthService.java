package com.example.Dating.service;

import com.example.Dating.dtos.request.LoginRequest;
import com.example.Dating.dtos.request.RegisterRequest;
import com.example.Dating.dtos.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}