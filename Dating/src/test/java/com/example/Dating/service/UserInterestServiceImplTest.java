package com.example.Dating.service;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.entities.Interest;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserInterest;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.repository.UserInterestRepository;
import com.example.Dating.service.impl.UserInterestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInterestServiceImplTest {

    @Mock private UserInterestRepository repository;
    @Mock private AuthService userService;

    @InjectMocks
    private UserInterestServiceImpl userInterestService;

    private UUID userId;
    private UUID interestId;
    private UserInterestRequest request;
    private User user;
    private Interest interest;
    private UserInterest userInterest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        interestId = UUID.randomUUID();
        request = new UserInterestRequest();
        request.setUserId(userId);
        request.setInterestId(interestId);

        user = User.builder().userId(userId).build();
        interest = Interest.builder().id(interestId).name("Music").build();
        userInterest = UserInterest.builder()
                .id(UUID.randomUUID())
                .user(user)
                .interest(interest)
                .build();
    }

    @Test
    void create_Success() {
        when(repository.existsByUser_UserIdAndInterest_Id(userId, interestId)).thenReturn(false);
        when(userService.findById(userId)).thenReturn(user);
        when(repository.save(any(UserInterest.class))).thenReturn(userInterest);

        UserInterestResponse response = userInterestService.create(request);

        assertThat(response.getId()).isEqualTo(userInterest.getId());
        assertThat(response.getInterestId()).isEqualTo(interestId);
        verify(repository).save(any(UserInterest.class));
    }

    @Test
    void create_Duplicate_Throws() {
        when(repository.existsByUser_UserIdAndInterest_Id(userId, interestId)).thenReturn(true);
        assertThatThrownBy(() -> userInterestService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getByUser_ReturnsList() {
        when(repository.findAllByUserProfile_UserId(userId)).thenReturn(List.of(userInterest));
        List<UserInterestResponse> responses = userInterestService.getByUser(userId);
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getInterestId()).isEqualTo(interestId);
    }

    @Test
    void delete_Success() {
        when(repository.existsByUser_UserIdAndInterest_Id(userId, interestId)).thenReturn(true);
        userInterestService.delete(userId, interestId);
        verify(repository).deleteByUserProfile_UserIdAndInterest_Id(userId, interestId);
    }

    @Test
    void delete_NotFound_Throws() {
        when(repository.existsByUser_UserIdAndInterest_Id(userId, interestId)).thenReturn(false);
        assertThatThrownBy(() -> userInterestService.delete(userId, interestId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}