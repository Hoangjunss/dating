package com.example.Dating.service;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.entities.UserMatch;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.mapper.UserMatchMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.UserMatchRepository;
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
    private final UserProfileService userProfileService;

    @Override
    public UserMatchResponse create(UUID userA, UUID userB) {

        // normalize order to avoid duplicate (A,B) and (B,A)
        UUID first = userA.compareTo(userB) < 0 ? userA : userB;
        UUID second = userA.compareTo(userB) < 0 ? userB : userA;

        repository.findByUserA_UserIdAndUserB_UserId(first, second)
                .ifPresent(m -> {
                    throw new RuntimeException("Match already exists");
                });

        UserProfile userFrist = getUserProfile(first);
        UserProfile userSecond = getUserProfile(second);

        UserMatch match = UserMatch.builder()
                .userA(userFrist)
                .userB(userSecond)
                .build();

        repository.save(match);

        return UserMatchMapper.toResponse(match);
    }

    @Override
    public Optional<UserMatchResponse> getMatchBetween(UUID userAId, UUID userBId) {

        return repository.findByUserA_UserIdAndUserB_UserId(userAId, userBId)
                .map(UserMatchMapper::toResponse);
    }

    @Override
    public void unmatch(UUID matchId) {

        UserMatch match = repository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        match.setActive(false);

        repository.save(match);
    }

    @Override
    public boolean hasActiveMatch(UUID userAId, UUID userBId) {
        log.debug("Checking if match exists");
        return repository.existsByUserA_UserIdAndUserB_UserIdAndActiveTrue(userAId, userBId);
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


    private UserProfile getUserProfile(UUID id) {
        return  userProfileService.findEntityById(id);
    }
}