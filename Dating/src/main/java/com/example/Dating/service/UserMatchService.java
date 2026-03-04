package com.example.Dating.service;

import com.example.Dating.dtos.response.UserMatchResponse;

import java.util.List;
import java.util.UUID;

public interface UserMatchService {

    UserMatchResponse create(UUID userA, UUID userB);

    List<UserMatchResponse> getMatches(UUID userId);

    void unmatch(UUID matchId);
}