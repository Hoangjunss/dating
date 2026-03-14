package com.example.Dating.service;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.entities.UserMatch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMatchService {

    UserMatchResponse create(UUID userA, UUID userB);

    Optional<UserMatchResponse> getMatchBetween(UUID userAId, UUID userBId);

    void unmatch(UUID matchId);

    boolean hasActiveMatch(UUID userAId,  UUID userBId);

    List<UserMatchResponse> getActiveMatches(UUID userId);

    List<UserMatchResponse> getAllMatches(UUID userId);
}