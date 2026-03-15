package com.example.Dating.service;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.entities.Interest;
import com.example.Dating.entities.UserInterest;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.UserInterestMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for UserInterest CRUD operations.
 * Manages the mapping between users and their interests.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserInterestServiceImpl implements UserInterestService {

    private final UserInterestRepository repository;
    private final UserProfileService userProfileService;

    /**
     * Adds an interest to a user.
     * Business rule: Each user-interest combination must be unique.
     */
    @Override
    @Transactional
    public UserInterestResponse create(UserInterestRequest request) {
        log.info("Adding interest {} to user {}", request.getInterestId(), request.getUserId());

        if (repository.existsByUserIdAndInterestId(request.getUserId(), request.getInterestId())) {
            log.warn("Interest already added for userId: {}, interestId: {}", request.getUserId(), request.getInterestId());
            throw new DuplicateResourceException("This interest is already added for this user");
        }

        UserInterest entity = UserInterestMapper.toEntity(request);
        UserProfile userProfile = UserProfileMapper.toEntity(userProfileService.get(request.getUserId()));
        Interest interest = Interest.builder().id(request.getInterestId()).build();
        entity.setUserProfile(userProfile);
        entity.setInterest(interest);
        repository.save(entity);

        log.info("Interest added successfully with id: {}", entity.getId());
        return UserInterestMapper.toResponse(entity);
    }

    /**
     * Retrieves all interests for a user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserInterestResponse> getByUser(UUID userId) {
        log.debug("Fetching interests for userId: {}", userId);
        
        return repository.findByUserId(userId)
                .stream()
                .map(UserInterestMapper::toResponse)
                .toList();
    }

    /**
     * Removes an interest from a user.
     */
    @Override
    @Transactional
    public void delete(UUID userId, UUID interestId) {
        log.info("Removing interest {} from user {}", interestId, userId);
        
        if (!repository.existsByUserIdAndInterestId(userId, interestId)) {
            log.warn("Interest not found for userId: {}, interestId: {}", userId, interestId);
            throw new ResourceNotFoundException("Interest not found for this user");
        }
        
        repository.deleteByUserIdAndInterestId(userId, interestId);
        log.info("Interest removed successfully for userId: {}, interestId: {}", userId, interestId);
    }
}