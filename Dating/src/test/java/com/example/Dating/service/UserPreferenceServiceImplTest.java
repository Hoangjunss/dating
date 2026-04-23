package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.entities.UserPreference;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.repository.UserPreferenceRepository;
import com.example.Dating.service.impl.UserPreferenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceImplTest {

    @Mock private UserPreferenceRepository repository;

    @InjectMocks
    private UserPreferenceServiceImpl preferenceService;

    private UUID userId;
    private UserPreferenceRequest request;
    private UserPreference preference;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        request = new UserPreferenceRequest();
        request.setGenderPreference("FEMALE");
        request.setMinAge(20);
        request.setMaxAge(30);
        request.setMaxDistanceKm(50);

        preference = UserPreference.builder()
                .userId(userId)
                .genderPreference("FEMALE")
                .minAge(20)
                .maxAge(30)
                .maxDistanceKm(50)
                .build();
    }

    @Test
    void save_CreateNew_Success() {
        when(repository.findById(userId)).thenReturn(Optional.empty());
        when(repository.save(any(UserPreference.class))).thenReturn(preference);

        UserPreferenceResponse response = preferenceService.save(userId, request);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getGenderPreference()).isEqualTo("FEMALE");
        verify(repository).save(any());
    }

    @Test
    void save_UpdateExisting_Success() {
        when(repository.findById(userId)).thenReturn(Optional.of(preference));
        when(repository.save(any(UserPreference.class))).thenReturn(preference);

        request.setMinAge(25); // update
        UserPreferenceResponse response = preferenceService.save(userId, request);

        assertThat(response.getMinAge()).isEqualTo(25); // updated
        verify(repository).save(any());
    }

    @Test
    void get_Success() {
        when(repository.findById(userId)).thenReturn(Optional.of(preference));
        UserPreferenceResponse response = preferenceService.get(userId);
        assertThat(response.getUserId()).isEqualTo(userId);
    }

    @Test
    void get_NotFound_Throws() {
        when(repository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> preferenceService.get(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_Success() {
        when(repository.existsById(userId)).thenReturn(true);
        preferenceService.delete(userId);
        verify(repository).deleteById(userId);
    }

    @Test
    void delete_NotFound_Throws() {
        when(repository.existsById(userId)).thenReturn(false);
        assertThatThrownBy(() -> preferenceService.delete(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}