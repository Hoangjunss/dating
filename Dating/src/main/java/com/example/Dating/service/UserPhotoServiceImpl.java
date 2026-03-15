package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.entities.UserPhoto;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.UserPhotoMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.UserPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for UserPhoto CRUD operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserPhotoServiceImpl implements UserPhotoService {

    private final UserPhotoRepository repository;
    private final UserProfileService userProfileService;

    /**
     * Creates a new user photo.
     */
    @Override
    @Transactional
    public UserPhotoResponse create(UserPhotoCreateRequest request) {
        log.info("Creating photo for userId: {}", request.getUserId());
        
        UserPhoto entity = UserPhotoMapper.toEntity(request);
        UserProfile userProfile = UserProfileMapper.toEntity(userProfileService.get(request.getUserId()));
        entity.setUserProfile(userProfile);
        repository.save(entity);
        
        log.info("Photo created successfully with id: {}", entity.getId());
        return UserPhotoMapper.toResponse(entity);
    }

    /**
     * Retrieves a photo by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public UserPhotoResponse get(UUID id) {
        log.debug("Fetching photo with id: {}", id);
        
        return repository.findById(id)
                .map(UserPhotoMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Photo not found with id: {}", id);
                    return new ResourceNotFoundException("Photo not found with id: " + id);
                });
    }

    /**
     * Retrieves all photos for a specific user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserPhotoResponse> getByUser(UUID userId) {
        log.debug("Fetching photos for userId: {}", userId);
        
        return repository.findByUserId(userId)
                .stream()
                .map(UserPhotoMapper::toResponse)
                .toList();
    }

    /**
     * Deletes a photo by ID.
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting photo with id: {}", id);
        
        if (!repository.existsById(id)) {
            log.warn("Photo not found for deletion with id: {}", id);
            throw new ResourceNotFoundException("Photo not found with id: " + id);
        }
        
        repository.deleteById(id);
        log.info("Photo deleted successfully with id: {}", id);
    }
}