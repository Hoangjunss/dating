package com.example.Dating.controller;

import com.example.Dating.dtos.request.LoginRequest;
import com.example.Dating.dtos.request.RefreshTokenRequest;
import com.example.Dating.dtos.request.RegisterRequest;
import com.example.Dating.dtos.response.AuthResponse;
import com.example.Dating.dtos.response.TokenResponse;
import com.example.Dating.dtos.response.UserResponse;
import com.example.Dating.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AuthService authService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void register_ValidRequest_Returns201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("Password123!");

        AuthResponse response = AuthResponse.builder()
                .userId(UUID.randomUUID())
                .username("newuser")
                .email("new@example.com")
                .hasProfile(false)
                .accessToken("access.token")
                .refreshToken("refresh.token")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.accessToken").value("access.token"));
    }

    @Test
    void login_ValidRequest_Returns200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        AuthResponse response = AuthResponse.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .hasProfile(true)
                .accessToken("access.token")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access.token"));
    }

    @Test
    void refreshToken_ValidRequest_Returns200() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid.refresh.token");

        AuthResponse response = AuthResponse.builder()
                .accessToken("new.access.token")
                .refreshToken("new.refresh.token")
                .build();

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new.access.token"));
    }

    @Test
    @WithMockUser
    void me_Authenticated_ReturnsUserAndToken() throws Exception {
        TokenResponse tokenResponse = TokenResponse.builder()
                .user(UserResponse.builder().userId(UUID.randomUUID()).username("me").build())
                .accessToken("new.token")
                .build();
        when(authService.me()).thenReturn(tokenResponse);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("me"))
                .andExpect(jsonPath("$.accessToken").value("new.token"));
    }
}