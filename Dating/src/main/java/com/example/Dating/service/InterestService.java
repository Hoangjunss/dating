package com.example.Dating.service;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;

import java.util.List;
import java.util.UUID;

public interface InterestService {

    /**
     * Creates a new interest.
     */
    InterestResponse create(InterestCreateRequest request);

    /**
     * Retrieves an interest by ID.
     */
    InterestResponse get(UUID id);

    /**
     * Retrieves all interests.
     */
    List<InterestResponse> getAll();

    /**
     * Deletes an interest by ID.
     */
    void delete(UUID id);
}