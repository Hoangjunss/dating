package com.example.Dating.service;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.entities.UserPresence;
import com.example.Dating.mapper.UserPresenceMapper;
import com.example.Dating.repository.UserPresenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPresenceServiceImpl implements UserPresenceService {

    private final UserPresenceRepository repository;

    @Override
    public void setOnline(UUID userId) {
        repository.save(
                UserPresence.builder()
                        .userId(userId)
                        .online(true)
                        .lastActiveAt(Instant.now())
                        .build()
        );
    }

    @Override
    public void setOffline(UUID userId) {
        repository.save(
                UserPresence.builder()
                        .userId(userId)
                        .online(false)
                        .lastActiveAt(Instant.now())
                        .build()
        );
    }

    @Override
    public UserPresenceResponse get(UUID userId) {
        return repository.findById(userId)
                .map(UserPresenceMapper::toResponse)
                .orElse(null);
    }
}