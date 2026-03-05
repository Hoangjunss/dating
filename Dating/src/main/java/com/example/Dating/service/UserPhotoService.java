package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;

import java.util.List;
import java.util.UUID;

public interface UserPhotoService {
    
    /**
     * Creates a new user photo.
     */
    UserPhotoResponse create(UserPhotoCreateRequest request);
    
    /**
     * Retrieves a photo by ID.
     */
    UserPhotoResponse get(UUID id);
    
    /**
     * Retrieves all photos for a specific user.
     */
    List<UserPhotoResponse> getByUser(UUID userId);
    
    /**
     * Deletes a photo by ID.
     */
    void delete(UUID id);
}