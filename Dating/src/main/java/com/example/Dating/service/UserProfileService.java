package com.example.Dating.service;


import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {

    UserProfileResponse create(UserProfileCreateRequest request);

    UserProfileResponse update(UUID userId, UserProfileUpdateRequest request);

    UserProfileResponse get(UUID userId);

    List<UserProfileResponse> getAll();

    void delete(UUID userId);
}