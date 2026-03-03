package com.example.Dating.service;

import com.example.Dating.dtos.response.UserPresenceResponse;

import java.util.UUID;

public interface UserPresenceService {

    void setOnline(UUID userId);

    void setOffline(UUID userId);

    UserPresenceResponse get(UUID userId);
}