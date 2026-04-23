package com.example.Dating.service.impl;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserMatch;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.mapper.UserMatchMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.UserMatchRepository;
import com.example.Dating.service.AuthService;
import com.example.Dating.service.UserMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles match business logic.
 * Match is created only when two users mutually like each other.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMatchServiceImpl implements UserMatchService {

    private final UserMatchRepository repository;
    private final AuthService userService;

    @Override
    public UserMatchResponse create(UUID userA, UUID userB) {

        // normalize order to avoid duplicate (A,B) and (B,A)
        UUID first = userA.compareTo(userB) < 0 ? userA : userB;
        UUID second = userA.compareTo(userB) < 0 ? userB : userA;

        repository.findByUserA_UserIdAndUserB_UserId(first, second)
                .ifPresent(m -> {
                    throw new RuntimeException("Match already exists");
                });

        User userFirst = getUser(first);
        User userSecond = getUser(second);

        UserMatch match = UserMatch.builder()
                .userA(userFirst)
                .userB(userSecond)
                .build();

        repository.save(match);

        return UserMatchMapper.toResponse(match);
    }

    public Optional<UserMatchResponse> getMatchBetween(UUID userAId, UUID userBId) {
        UUID first = userAId.compareTo(userBId) < 0 ? userAId : userBId;
        UUID second = userAId.compareTo(userBId) < 0 ? userBId : userAId;

        return repository.findByUserA_UserIdAndUserB_UserId(first, second)
                .map(UserMatchMapper::toResponse);
    }
    @Override
    public void unmatch(UUID matchId, UUID requesterId) {

        UserMatch match = repository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        boolean isMember = match.getUserA().getUserId().equals(requesterId)
                || match.getUserB().getUserId().equals(requesterId);
        if (!isMember) {
            throw new ValidationException("You are not a member of this match");
        }

        match.setActive(false);

        repository.save(match);

        log.info("Match {} unmatch bởi requesterId: {}", matchId, requesterId);
    }

    @Override
    public boolean hasActiveMatch(UUID userAId, UUID userBId) {
        log.debug("Checking if match exists");
        UUID first = userAId.compareTo(userBId) < 0 ? userAId : userBId;
        UUID second = userAId.compareTo(userBId) < 0 ? userBId : userAId;

        return repository.existsByUserA_UserIdAndUserB_UserIdAndActiveTrue(first, second);
    }
    @Override
    public List<UserMatchResponse> getActiveMatches(UUID userId) {
        return repository.findActiveMatchesByUserId(userId)
                .stream()
                .map(UserMatchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserMatchResponse> getAllMatches(UUID userId) {
        return repository.findAllByUserId(userId)
                .stream()
                .map(UserMatchMapper::toResponse)
                .collect(Collectors.toList());
    }


    private User getUser(UUID id) {
        return  userService.findById(id);
    }
}