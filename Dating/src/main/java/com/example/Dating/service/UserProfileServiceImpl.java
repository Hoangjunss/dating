package com.example.Dating.service;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.*;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserPhoto;
import com.example.Dating.entities.UserPreference;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.UserPhotoRepository;
import com.example.Dating.repository.UserProfileRepository;
import com.example.Dating.repository.UserRepository;
import com.example.Dating.specification.UserProfileSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserPreferenceService userPreferenceService;
    private final UserPhotoRepository userPhotoRepository;
    private final UserInterestService userInterestService;
    private final InterestService interestService;


    /**
     * Create UserProfile (after registration).
     * Flow:
     * 1. Validate that userId exists in the users table
     * 2. Check that User does not already have a profile (prevent creating twice)
     * 3. Save the new UserProfile
     * 4. Backlink: user.profile = userProfile → save User
     */
    @Override
    @Transactional
    public UserProfileResponse create(UserProfileCreateRequest request) {
        log.info("Creating UserProfile for userId: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));

        if (user.getProfile() != null) {
            throw new DuplicateResourceException(
                    "UserProfile already exists for userId: " + request.getUserId());
        }

        UserProfile profile = UserProfileMapper.toEntity(request);

        profile.setUser(user);
        userProfileRepository.save(profile);

        log.info("UserProfile saved with userId: {}", profile.getUser());

        return UserProfileMapper.toResponse(profile);
    }
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse get(UUID userId) {
        log.debug("Fetching profile for userId: {}", userId);

        // 1. Tìm UserProfile (Entity này đã có sẵn User bên trong nhờ @OneToOne)
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        // 2. Chuyển đổi sang Response DTO cơ bản
        UserProfileResponse response = UserProfileMapper.toResponse(userProfile);

        // 3. Lấy danh sách Interest IDs từ bảng trung gian UserInterest
        List<UserInterestResponse> userInterests = userInterestService.getByUser(userId);

        // 4. Duyệt qua danh sách ID đó để lấy Name từ InterestService
        List<InterestResponse> interests = userInterests.stream()
                .map(ui -> {
                    // Gọi hàm get(UUID id) mà Dung vừa viết ở trên
                    try {
                        return interestService.get(ui.getInterestId());
                    } catch (Exception e) {
                        return null; // Phòng trường hợp interest bị xóa khỏi master data
                    }
                })
                .filter(Objects::nonNull) // Loại bỏ các giá trị null
                .toList();

        response.setInterestResponses(interests);
        UserPhoto userPhoto=userPhotoRepository.findByUserProfile_Id(userId);
        response.setImage(userPhoto.getUrl());

        return response;
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
        
        return userProfileRepository.findAll()
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
    public Page<UserProfileResponse> getAllPaginated(UUID userId, Pageable pageable) {

        UserPreferenceResponse pref = userPreferenceService.get(userId);
        UserProfile currentUser = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<UserProfile> spec = UserProfileSpecification
                .filter(userId, currentUser, pref);

        return userProfileRepository.findAll(spec, pageable)
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
        
        UserProfile entity = findById(userId);

        UserProfileMapper.updateEntity(entity, request);
        userProfileRepository.save(entity);
        
        log.info("Profile updated successfully for userId: {}", userId);
        return UserProfileMapper.toResponse(entity);
    }

    @Override
    public UserProfile findEntityById(UUID id) {
        log.debug("Fetching profile for userId: {}", id);
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
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
        
        if (!userProfileRepository.existsById(userId)) {
            log.warn("Profile not found for deletion, userId: {}", userId);
            throw new ResourceNotFoundException("Profile not found with userId: " + userId);
        }

        userProfileRepository.deleteById(userId);
        log.info("Profile deleted successfully for userId: {}", userId);
    }

    private UserProfile findById(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Profile not found for update, userId: {}", id);
                    return new ResourceNotFoundException("Profile not found with userId: " + id);
                });
    }
}