package com.example.Dating.service;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer responsible for handling business logic related to UserProfile.
 *
 * Responsibilities:
 * - Validate business rules
 * - Coordinate repository operations
 * - Convert between DTOs and entities
 * - Handle transactions
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;

    /**
     * Creates a new user profile.
     * Business rule: displayName must be unique.
     * 
     * @param request User profile creation request
     * @return Created user profile response
     * @throws DuplicateResourceException if displayName already exists
     */
    @Override
    @Transactional
    public UserProfileResponse create(UserProfileCreateRequest request) {
        log.info("Creating user profile with displayName: {}", request.getDisplayName());
        
        if (repository.existsByDisplayName(request.getDisplayName())) {
            log.warn("Display name already exists: {}", request.getDisplayName());
            throw new DuplicateResourceException("Display name '" + request.getDisplayName() + "' already exists");
        }

        UserProfile entity = UserProfileMapper.toEntity(request);
        repository.save(entity);
        
        log.info("Profile created successfully with userId: {}", entity.getUserId());
        return UserProfileMapper.toResponse(entity);
    }

    /**
     * Retrieves a single profile by userId.
     *
     * @param userId The user ID
     * @return User profile response
     * @throws ResourceNotFoundException if profile not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse get(UUID userId) {
        log.debug("Fetching profile for userId: {}", userId);
        
        return repository.findById(userId)
                .map(UserProfileMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Profile not found for userId: {}", userId);
                    return new ResourceNotFoundException("Profile not found with userId: " + userId);
                });
    }

    /**
     * Retrieves all profiles in the system.
     * 
     * @return List of all user profiles
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAll() {
        log.debug("Fetching all profiles");
        
        return repository.findAll()
                .stream()
                .map(UserProfileMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves profiles with pagination and sorting support.
     * 
     * @param pageable Pagination and sorting parameters
     * @return Paginated user profiles
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllPaginated(Pageable pageable) {
        log.debug("Fetching profiles with pagination: {}", pageable);
        
        return repository.findAll(pageable)
                .map(UserProfileMapper::toResponse);
    }

    /**
     * Updates an existing profile using partial update.
     * Only non-null fields from request will be updated.
     * 
     * @param userId The user ID
     * @param request Update request
     * @return Updated user profile response
     * @throws ResourceNotFoundException if profile not found
     */
    @Override
    @Transactional
    public UserProfileResponse update(UUID userId, UserProfileUpdateRequest request) {
        log.info("Updating profile for userId: {}", userId);
        
        UserProfile entity = repository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Profile not found for update, userId: {}", userId);
                    return new ResourceNotFoundException("Profile not found with userId: " + userId);
                });

        UserProfileMapper.updateEntity(entity, request);
        repository.save(entity);
        
        log.info("Profile updated successfully for userId: {}", userId);
        return UserProfileMapper.toResponse(entity);
    }

    /**
     * Deletes a profile by userId.
     * 
     * @param userId The user ID
     * @throws ResourceNotFoundException if profile not found
     */
    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info("Deleting profile for userId: {}", userId);
        
        if (!repository.existsById(userId)) {
            log.warn("Profile not found for deletion, userId: {}", userId);
            throw new ResourceNotFoundException("Profile not found with userId: " + userId);
        }
        
        repository.deleteById(userId);
        log.info("Profile deleted successfully for userId: {}", userId);
    }
}