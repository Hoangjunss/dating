package com.example.Dating.service;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;

import java.util.List;
import java.util.UUID;

public interface UserInterestService {

    UserInterestResponse add(UserInterestRequest r);

    List<UserInterestResponse> getByUser(UUID userId);

    void remove(UUID userId, UUID interestId);
}