package com.example.Dating.service;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.entities.UserMatch;
import com.example.Dating.mapper.UserMatchMapper;
import com.example.Dating.repository.UserMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles match business logic.
 * Match is created only when two users mutually like each other.
 */
@Service
@RequiredArgsConstructor
public class UserMatchServiceImpl implements UserMatchService {

    private final UserMatchRepository repository;

    @Override
    public UserMatchResponse create(UUID userA, UUID userB) {

        // normalize order to avoid duplicate (A,B) and (B,A)
        UUID first = userA.compareTo(userB) < 0 ? userA : userB;
        UUID second = userA.compareTo(userB) < 0 ? userB : userA;

        repository.findByUserAIdAndUserBId(first, second)
                .ifPresent(m -> {
                    throw new RuntimeException("Match already exists");
                });

        UserMatch match = UserMatch.builder()
                .userAId(first)
                .userBId(second)
                .build();

        repository.save(match);

        return UserMatchMapper.toResponse(match);
    }

    @Override
    public List<UserMatchResponse> getMatches(UUID userId) {

        return repository.findByUserAIdOrUserBId(userId, userId)
                .stream()
                .map(UserMatchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void unmatch(UUID matchId) {

        UserMatch match = repository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        match.setActive(false);

        repository.save(match);
    }
}