package com.example.Dating.service;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.repository.InterestRepository;
import com.example.Dating.repository.UserPhotoRepository;
import com.example.Dating.repository.UserProfileRepository;
import com.example.Dating.repository.UserRepository;
import com.example.Dating.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock private UserProfileRepository userProfileRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserPreferenceService userPreferenceService;
    @Mock private UserPhotoRepository userPhotoRepository;
    @Mock private UserInterestService userInterestService;
    @Mock private InterestRepository interestRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UUID userId;
    private User user;
    private UserProfileCreateRequest createRequest;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().userId(userId).username("testuser").build();
        createRequest = new UserProfileCreateRequest();
        createRequest.setUserId(userId);
        createRequest.setDisplayName("Test User");
        createRequest.setGender("MALE");
        createRequest.setBirthday(LocalDate.of(2000, 1, 1));
        profile = UserProfile.builder()
                .id(userId)
                .user(user)
                .displayName("Test User")
                .gender("MALE")
                .build();
    }

    @Test
    void create_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.getProfile()).thenReturn(null);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);

        UserProfileResponse response = userProfileService.create(createRequest);
        assertThat(response.getDisplayName()).isEqualTo("Test User");
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void create_UserAlreadyHasProfile_Throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user.setProfile(profile);
        assertThatThrownBy(() -> userProfileService.create(createRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void create_UserNotFound_Throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userProfileService.create(createRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void get_ProfileExists_ReturnsResponse() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(userInterestService.getByUser(userId)).thenReturn(List.of());
        when(interestRepository.findById(any())).thenReturn(Optional.empty());
        when(userPhotoRepository.findFirstByUserProfile_User_UserIdAndIsPrimaryTrue(userId)).thenReturn(Optional.empty());

        UserProfileResponse response = userProfileService.get(userId);
        assertThat(response.getDisplayName()).isEqualTo("Test User");
    }

    @Test
    void get_ProfileNotFound_Throws() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userProfileService.get(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_Success() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setBio("New bio");
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);

        UserProfileResponse response = userProfileService.update(userId, updateRequest);
        assertThat(response.getBio()).isEqualTo("New bio");
    }

    @Test
    void delete_Success() {
        when(userProfileRepository.existsById(userId)).thenReturn(true);
        userProfileService.delete(userId);
        verify(userProfileRepository).deleteById(userId);
    }

    @Test
    void delete_ProfileNotFound_Throws() {
        when(userProfileRepository.existsById(userId)).thenReturn(false);
        assertThatThrownBy(() -> userProfileService.delete(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}