package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.entities.UserPreference;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.UserPreferenceMapper;
import com.example.Dating.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer for UserPreference CRUD operations.
 * Manages user search and matching preferences.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceRepository repository;

    /**
     * Creates or updates user preferences.
     * Follows upsert pattern: creates if not exists, updates if exists.
     */
    @Override
    @Transactional
    public UserPreferenceResponse save(UUID userId, UserPreferenceRequest request) {
        log.info("Saving preferences for userId: {}", userId);

        UserPreference entity = repository.findById(userId)
                .orElse(UserPreferenceMapper.toEntity(userId, request));

        UserPreferenceMapper.update(entity, request);
        repository.save(entity);

        log.info("Preferences saved successfully for userId: {}", userId);
        return UserPreferenceMapper.toResponse(entity);
    }

    /**
     * Retrieves user preferences by userId.
     */
    @Override
    @Transactional(readOnly = true)
    public UserPreferenceResponse get(UUID userId) {
        log.debug("Fetching preferences for userId: {}", userId);
        
        return repository.findById(userId)
                .map(UserPreferenceMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Preferences not found for userId: {}", userId);
                    return new ResourceNotFoundException("Preferences not found for userId: " + userId);
                });
    }

    /**
     * Deletes user preferences.
     */
    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info("Deleting preferences for userId: {}", userId);
        
        if (!repository.existsById(userId)) {
            log.warn("Preferences not found for deletion, userId: {}", userId);
            throw new ResourceNotFoundException("Preferences not found for userId: " + userId);
        }
        
        repository.deleteById(userId);
        log.info("Preferences deleted successfully for userId: {}", userId);
    }
}