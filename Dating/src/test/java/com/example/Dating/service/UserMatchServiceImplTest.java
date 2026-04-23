package com.example.Dating.service;

import com.example.Dating.entities.User;
import com.example.Dating.entities.UserMatch;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.UserMatchRepository;
import com.example.Dating.service.impl.UserMatchServiceImpl;
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
class UserMatchServiceImplTest {

    @Mock private UserMatchRepository repository;
    @Mock private AuthService userService;

    @InjectMocks
    private UserMatchServiceImpl matchService;

    private UUID matchId, userAId, userBId;
    private User userA, userB;
    private UserMatch match;

    @BeforeEach
    void setUp() {
        matchId = UUID.randomUUID();
        userAId = UUID.randomUUID();
        userBId = UUID.randomUUID();
        userA = User.builder().userId(userAId).username("A").build();
        userB = User.builder().userId(userBId).username("B").build();
        match = UserMatch.builder().id(matchId).userA(userA).userB(userB).active(true).build();
    }

    @Test
    void create_Success() {
        when(userService.findById(userAId)).thenReturn(userA);
        when(userService.findById(userBId)).thenReturn(userB);
        when(repository.findByUserA_UserIdAndUserB_UserId(any(), any())).thenReturn(Optional.empty());
        when(repository.save(any(UserMatch.class))).thenReturn(match);

        var response = matchService.create(userAId, userBId);
        assertThat(response.getId()).isEqualTo(matchId);
        verify(repository).save(any());
    }

    @Test
    void unmatch_ValidUser_SetsInactive() {
        when(repository.findById(matchId)).thenReturn(Optional.of(match));
        matchService.unmatch(matchId, userAId);
        assertThat(match.getActive()).isFalse();
        verify(repository).save(match);
    }

    @Test
    void unmatch_NotMember_Throws() {
        when(repository.findById(matchId)).thenReturn(Optional.of(match));
        assertThatThrownBy(() -> matchService.unmatch(matchId, UUID.randomUUID()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void hasActiveMatch_ReturnsTrue() {
        when(repository.existsByUserA_UserIdAndUserB_UserIdAndActiveTrue(userAId, userBId)).thenReturn(true);
        assertThat(matchService.hasActiveMatch(userAId, userBId)).isTrue();
    }

    @Test
    void getActiveMatches_ReturnsList() {
        when(repository.findActiveMatchesByUserId(userAId)).thenReturn(java.util.List.of(match));
        var matches = matchService.getActiveMatches(userAId);
        assertThat(matches).hasSize(1);
    }
}