package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;

import java.util.UUID;

public interface UserPreferenceService {

    UserPreferenceResponse save(UUID userId, UserPreferenceRequest r);

    UserPreferenceResponse get(UUID userId);
}