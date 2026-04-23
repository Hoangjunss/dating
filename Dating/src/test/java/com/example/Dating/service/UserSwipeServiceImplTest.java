package com.example.Dating.service;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResultResponse;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserSwipe;
import com.example.Dating.repository.UserSwipeRepository;
import com.example.Dating.service.impl.UserSwipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSwipeServiceImplTest {

    @Mock private UserSwipeRepository swipeRepository;
    @Mock private AuthService userService;
    @Mock private UserMatchService userMatchService;
    @Mock private ConversationService conversationService;
    @Mock private RecommendationService recommendationService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private UserSwipeServiceImpl swipeService;

    private UUID fromId, toId;
    private User fromUser, toUser;
    private SwipeRequest likeRequest, skipRequest;

    @BeforeEach
    void setUp() {
        fromId = UUID.randomUUID();
        toId = UUID.randomUUID();
        fromUser = User.builder().userId(fromId).build();
        toUser = User.builder().userId(toId).build();

        likeRequest = new SwipeRequest();
        likeRequest.setToUserId(toId);
        likeRequest.setLiked(true);

        skipRequest = new SwipeRequest();
        skipRequest.setToUserId(toId);
        skipRequest.setLiked(false);
    }

    @Test
    void swipe_LikeAndMutualLike_CreatesMatchAndConversation() {
        when(userService.findById(fromId)).thenReturn(fromUser);
        when(userService.findById(toId)).thenReturn(toUser);
        when(swipeRepository.existsByFromUser_UserIdAndToUser_UserId(fromId, toId)).thenReturn(false);
        when(swipeRepository.saveAndFlush(any(UserSwipe.class))).thenAnswer(inv -> inv.getArgument(0));
        // mutual like exists: toUser already liked fromUser
        when(swipeRepository.existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue(toId, fromId)).thenReturn(true);
        when(userMatchService.create(fromId, toId)).thenReturn(any());
        when(conversationService.createOrGet(fromId, toId, true)).thenReturn(any());

        SwipeResultResponse response = swipeService.swipe(likeRequest, fromId);

        assertThat(response.isMutualLike()).isTrue();
        assertThat(response.getMatchId()).isNotNull();
        verify(recommendationService).processSwipeElo(fromId, toId, true);
        verify(userMatchService).create(fromId, toId);
        verify(notificationService, times(2)).createAndPush(any(), any(), any(), any(), any(), any());
    }

    @Test
    void swipe_LikeWithoutMutual_OnlyUpdatesElo() {
        when(userService.findById(fromId)).thenReturn(fromUser);
        when(userService.findById(toId)).thenReturn(toUser);
        when(swipeRepository.existsByFromUser_UserIdAndToUser_UserId(fromId, toId)).thenReturn(false);
        when(swipeRepository.existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue(toId, fromId)).thenReturn(false);

        SwipeResultResponse response = swipeService.swipe(likeRequest, fromId);

        assertThat(response.isMutualLike()).isFalse();
        verify(recommendationService).processSwipeElo(fromId, toId, true);
        verify(userMatchService, never()).create(any(), any());
    }

    @Test
    void swipe_AlreadySwiped_ThrowsException() {
        when(swipeRepository.existsByFromUser_UserIdAndToUser_UserId(fromId, toId)).thenReturn(true);
        assertThatThrownBy(() -> swipeService.swipe(likeRequest, fromId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already swiped");
    }

    @Test
    void swipe_SelfSwipe_ThrowsException() {
        likeRequest.setToUserId(fromId);
        assertThatThrownBy(() -> swipeService.swipe(likeRequest, fromId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot swipe yourself");
    }

    @Test
    void isMatch_ReturnsTrue_WhenBothLiked() {
        when(swipeRepository.existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue(fromId, toId)).thenReturn(true);
        boolean match = swipeService.isMatch(fromId, toId);
        assertThat(match).isTrue();
    }
}