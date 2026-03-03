package com.example.Dating.service;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer responsible for handling business logic
 * related to UserProfile.
 *
 * Responsibilities:
 * - Validate business rules
 * - Coordinate repository operations
 * - Convert between DTOs and entities
 *
 * This layer should NOT contain persistence logic directly.
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;

    /**
     * Creates a new user profile.
     * Business rule: displayName must be unique.
     */
    @Override
    public UserProfileResponse create(UserProfileCreateRequest request) {

        if (repository.existsByDisplayName(request.getDisplayName())) {
            throw new RuntimeException("Display name already exists");
        }

        UserProfile entity = UserProfileMapper.toEntity(request);

        repository.save(entity);

        return UserProfileMapper.toResponse(entity);
    }

    /**
     * Updates an existing profile using partial update.
     * Only non-null fields from request will be updated.
     */
    @Override
    public UserProfileResponse update(UUID userId, UserProfileUpdateRequest request) {

        UserProfile entity = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        UserProfileMapper.updateEntity(entity, request);

        repository.save(entity);

        return UserProfileMapper.toResponse(entity);
    }

    /**
     * Retrieves a single profile by userId.
     */
    @Override
    public UserProfileResponse get(UUID userId) {
        return repository.findById(userId)
                .map(UserProfileMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    /**
     * Retrieves all profiles in the system.
     * Should be paginated in production environments.
     */
    @Override
    public List<UserProfileResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(UserProfileMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a profile by userId.
     */
    @Override
    public void delete(UUID userId) {
        repository.deleteById(userId);
    }
}