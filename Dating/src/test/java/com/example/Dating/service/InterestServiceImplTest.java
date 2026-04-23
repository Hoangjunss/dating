package com.example.Dating.service;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;
import com.example.Dating.entities.Interest;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.repository.InterestRepository;
import com.example.Dating.service.impl.InterestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestServiceImplTest {

    @Mock private InterestRepository repository;

    @InjectMocks
    private InterestServiceImpl interestService;

    private UUID interestId;
    private Interest interest;
    private InterestCreateRequest request;

    @BeforeEach
    void setUp() {
        interestId = UUID.randomUUID();
        interest = Interest.builder().id(interestId).name("Music").build();
        request = new InterestCreateRequest();
        request.setName("Music");
    }

    @Test
    void create_Success() {
        when(repository.existsByName("Music")).thenReturn(false);
        when(repository.save(any(Interest.class))).thenReturn(interest);

        InterestResponse response = interestService.create(request);

        assertThat(response.getId()).isEqualTo(interestId);
        assertThat(response.getName()).isEqualTo("Music");
    }

    @Test
    void create_Duplicate_Throws() {
        when(repository.existsByName("Music")).thenReturn(true);
        assertThatThrownBy(() -> interestService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void get_Success() {
        when(repository.findById(interestId)).thenReturn(Optional.of(interest));
        InterestResponse response = interestService.get(interestId);
        assertThat(response.getName()).isEqualTo("Music");
    }

    @Test
    void get_NotFound_Throws() {
        when(repository.findById(interestId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> interestService.get(interestId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_ReturnsList() {
        when(repository.findAll()).thenReturn(List.of(interest));
        List<InterestResponse> responses = interestService.getAll();
        assertThat(responses).hasSize(1);
    }

    @Test
    void delete_Success() {
        when(repository.existsById(interestId)).thenReturn(true);
        interestService.delete(interestId);
        verify(repository).deleteById(interestId);
    }
}