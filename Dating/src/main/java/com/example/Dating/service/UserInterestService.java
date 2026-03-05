package com.example.Dating.service;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;

import java.util.List;
import java.util.UUID;

public interface UserInterestService {

    /**
     * Adds an interest to a user.
     */
    UserInterestResponse create(UserInterestRequest request);

    /**
     * Retrieves all interests for a user.
     */
    List<UserInterestResponse> getByUser(UUID userId);

    /**
     * Removes an interest from a user.
     */
    void delete(UUID userId, UUID interestId);
}