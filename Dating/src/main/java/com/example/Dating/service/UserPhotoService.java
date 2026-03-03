package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;

import java.util.List;
import java.util.UUID;

public interface UserPhotoService {
    UserPhotoResponse create(UserPhotoCreateRequest r);
    List<UserPhotoResponse> getByUser(UUID userId);
    void delete(UUID id);
}