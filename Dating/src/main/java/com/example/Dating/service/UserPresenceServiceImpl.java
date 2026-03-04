package com.example.Dating.service;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.entities.UserPresence;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.UserPresenceMapper;
import com.example.Dating.repository.UserPresenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service layer for UserPresence CRUD operations.
 * Manages user online/offline status tracking.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserPresenceServiceImpl implements UserPresenceService {

    private final UserPresenceRepository repository;

    /**
     * Updates user presence to online.
     */
    @Override
    @Transactional
    public void setOnline(UUID userId) {
        log.info("Setting user {} to online", userId);
        
        UserPresence presence = repository.findById(userId)
                .orElse(UserPresence.builder()
                        .userId(userId)
                        .build());

        presence.setOnline(true);
        presence.setLastActiveAt(Instant.now());
        repository.save(presence);
        
        log.info("User {} is now online", userId);
    }

    /**
     * Updates user presence to offline.
     */
    @Override
    @Transactional
    public void setOffline(UUID userId) {
        log.info("Setting user {} to offline", userId);
        
        UserPresence presence = repository.findById(userId)
                .orElse(UserPresence.builder()
                        .userId(userId)
                        .build());

        presence.setOnline(false);
        presence.setLastActiveAt(Instant.now());
        repository.save(presence);
        
        log.info("User {} is now offline", userId);
    }

    /**
     * Retrieves user presence status.
     */
    @Override
    @Transactional(readOnly = true)
    public UserPresenceResponse get(UUID userId) {
        log.debug("Fetching presence for userId: {}", userId);
        
        return repository.findById(userId)
                .map(UserPresenceMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Presence not found for userId: {}", userId);
                    return new ResourceNotFoundException("Presence information not found for userId: " + userId);
                });
    }

    /**
     * Deletes user presence data.
     */
    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info("Deleting presence for userId: {}", userId);
        
        if (!repository.existsById(userId)) {
            log.warn("Presence not found for deletion, userId: {}", userId);
            throw new ResourceNotFoundException("Presence not found for userId: " + userId);
        }
        
        repository.deleteById(userId);
        log.info("Presence deleted successfully for userId: {}", userId);
    }
}