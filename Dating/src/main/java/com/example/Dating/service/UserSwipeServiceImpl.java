package com.example.Dating.service;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.dtos.response.SwipeResultResponse;
import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.entities.NotificationType;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserSwipe;
import com.example.Dating.repository.UserSwipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSwipeServiceImpl implements UserSwipeService {

    private final UserSwipeRepository swipeRepository;
    private final AuthService userService;
    private final UserMatchService userMatchService;
    private final ConversationService conversationService;
    private final RecommendationService recommendationService;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public SwipeResultResponse swipe(SwipeRequest request) {
        log.info("swipe request: {}", request);
        UUID fromId = request.getFromUserId();
        UUID toId   = request.getToUserId();

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot swipe yourself");
        }

        if (swipeRepository.existsByFromUser_UserIdAndToUser_UserId(fromId, toId)) {
            throw new IllegalStateException("You have already swiped this user");
        }

        User fromUser = userService.findById(fromId);
        User toUser   = userService.findById(toId);

        UserSwipe swipe = UserSwipe.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .isLiked(request.isLiked())
                .build();

        swipe = swipeRepository.saveAndFlush(swipe);

        recommendationService.processSwipeElo(fromId, toId, request.isLiked());

        boolean isMutualLike = request.isLiked() &&
                swipeRepository.existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue(toId, fromId);

        UUID matchId = null;
        UUID conversationId = null;

        if (isMutualLike) {
            UserMatchResponse matchResp = userMatchService.create(fromId, toId);
            matchId = matchResp.getId();

            ConversationResponse convResp = conversationService.createOrGet(fromId, toId, true);
            conversationId = convResp.getId();

            String title = "Trùng hợp!";
            notificationService.createAndPush(
                    fromId,
                    NotificationType.NEW_MATCH,
                    title,
                    "Bạn và " + toUser.getUsername() + " đã thích nhau — hãy bắt đầu trò chuyện!",
                    conversationId,
                    toId
            );
            notificationService.createAndPush(
                    toId,
                    NotificationType.NEW_MATCH,
                    title,
                    "Bạn và " + fromUser.getUsername() + " đã thích nhau — hãy bắt đầu trò chuyện!",
                    conversationId,
                    fromId
            );
        }

        return new SwipeResultResponse(
                swipe.getId(),
                swipe.isLiked(),
                isMutualLike,
                matchId,
                conversationId
        );
    }

    /**
     * A match happens when:
     * A likes B AND B likes A
     */
    @Override
    public boolean isMatch(UUID userA, UUID userB) {
    log.debug("Checking if UserA {} and UserB {}", userA, userB);
        return swipeRepository.existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue(userA, userB);
    }
}