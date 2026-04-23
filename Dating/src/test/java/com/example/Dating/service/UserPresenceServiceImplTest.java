package com.example.Dating.service;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.entities.UserPresence;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.repository.UserPresenceRepository;
import com.example.Dating.service.impl.UserPresenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPresenceServiceImplTest {

    @Mock private UserPresenceRepository repository;

    @InjectMocks
    private UserPresenceServiceImpl presenceService;

    private UUID userId;
    private UserPresence presence;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        presence = UserPresence.builder()
                .userId(userId)
                .online(true)
                .lastActiveAt(Instant.now())
                .build();
    }

    @Test
    void setOnline_New_ShouldCreate() {
        when(repository.findById(userId)).thenReturn(Optional.empty());
        when(repository.save(any(UserPresence.class))).thenReturn(presence);

        presenceService.setOnline(userId);

        verify(repository).save(any(UserPresence.class));
    }

    @Test
    void setOnline_Existing_ShouldUpdate() {
        when(repository.findById(userId)).thenReturn(Optional.of(presence));
        presenceService.setOnline(userId);
        assertThat(presence.isOnline()).isTrue();
        verify(repository).save(presence);
    }

    @Test
    void setOffline_ShouldSetOffline() {
        when(repository.findById(userId)).thenReturn(Optional.of(presence));
        presenceService.setOffline(userId);
        assertThat(presence.isOnline()).isFalse();
        verify(repository).save(presence);
    }

    @Test
    void get_Success() {
        when(repository.findById(userId)).thenReturn(Optional.of(presence));
        UserPresenceResponse response = presenceService.get(userId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.isOnline()).isTrue();
    }

    @Test
    void get_NotFound_Throws() {
        when(repository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> presenceService.get(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_Success() {
        when(repository.existsById(userId)).thenReturn(true);
        presenceService.delete(userId);
        verify(repository).deleteById(userId);
    }
}