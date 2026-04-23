package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.entities.UserPhoto;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.UserPhotoRepository;
import com.example.Dating.service.impl.UserPhotoServiceImpl;
import com.example.Dating.utils.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPhotoServiceImplTest {

    @Mock private UserPhotoRepository repository;
    @Mock private UserProfileService userProfileService;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserPhotoServiceImpl userPhotoService;

    private UUID userId;
    private UUID photoId;
    private UserProfile userProfile;
    private UserPhoto userPhoto;
    private UserPhotoCreateRequest request;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        photoId = UUID.randomUUID();
        userProfile = UserProfile.builder().id(userId).build();
        userPhoto = UserPhoto.builder()
                .id(photoId)
                .userProfile(userProfile)
                .url("http://cloudinary.com/photo.jpg")
                .sortOrder(1)
                .isPrimary(true)
                .build();
        request = new UserPhotoCreateRequest();
        request.setUserId(userId);
        request.setSortOrder(1);
        request.setIsPrimary(true);
        mockFile = mock(MultipartFile.class);
        request.setImage(mockFile);
    }

    @Test
    void create_Success() {
        when(userProfileService.findEntityById(userId)).thenReturn(userProfile);
        when(cloudinaryService.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn(Map.of("url", "http://cloudinary.com/photo.jpg"));
        when(repository.save(any(UserPhoto.class))).thenReturn(userPhoto);

        UserPhotoResponse response = userPhotoService.create(request);

        assertThat(response.getId()).isEqualTo(photoId);
        assertThat(response.getUrl()).isEqualTo("http://cloudinary.com/photo.jpg");
        verify(repository).save(any(UserPhoto.class));
    }

    @Test
    void get_Success() {
        when(repository.findById(photoId)).thenReturn(Optional.of(userPhoto));
        UserPhotoResponse response = userPhotoService.get(photoId);
        assertThat(response.getId()).isEqualTo(photoId);
    }

    @Test
    void get_NotFound_Throws() {
        when(repository.findById(photoId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userPhotoService.get(photoId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByUser_ReturnsList() {
        when(repository.findByUserProfile_User_UserId(userId)).thenReturn(List.of(userPhoto));
        List<UserPhotoResponse> responses = userPhotoService.getByUser(userId);
        assertThat(responses).hasSize(1);
    }

    @Test
    void delete_Success() {
        when(repository.findById(photoId)).thenReturn(Optional.of(userPhoto));
        userPhotoService.delete(photoId, userId);
        verify(repository).deleteById(photoId);
    }

    @Test
    void delete_NotOwner_Throws() {
        UUID otherUserId = UUID.randomUUID();
        when(repository.findById(photoId)).thenReturn(Optional.of(userPhoto));
        assertThatThrownBy(() -> userPhotoService.delete(photoId, otherUserId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("only delete your own photos");
    }
}