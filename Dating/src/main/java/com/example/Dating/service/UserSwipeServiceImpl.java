package com.example.Dating.service;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.dtos.response.SwipeResponse;
import com.example.Dating.dtos.response.SwipeResultResponse;
import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.entities.Conversation;
import com.example.Dating.entities.UserMatch;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.entities.UserSwipe;
import com.example.Dating.mapper.UserMatchMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.mapper.UserSwipeMapper;
import com.example.Dating.repository.UserSwipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.model.ast.builder.CollectionRowDeleteByUpdateSetNullBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSwipeServiceImpl implements UserSwipeService {

    private final UserSwipeRepository swipeRepository;
    private final UserProfileService userProfileService;
    private final UserMatchService userMatchService;
    private final ConversationService conversationService;

    @Transactional
    @Override
    public SwipeResultResponse swipe(SwipeRequest request) {
        UUID fromId = request.getFromUserId();
        UUID toId   = request.getToUserId();

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot swipe yourself");
        }

        if (swipeRepository.existsByFromUser_IdAndToUser_Id(fromId, toId)) {
            throw new IllegalStateException("You have already swiped this user");
        }

        UserProfile fromUser = userProfileService.findEntityById(fromId);
        UserProfile toUser   = userProfileService.findEntityById(toId);

        UserSwipe swipe = UserSwipe.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .isLiked(request.isLiked())
                .build();

        swipe = swipeRepository.save(swipe);

        boolean isMutualLike = request.isLiked() &&
                swipeRepository.existsMutualLike(fromId, toId);

        UUID matchId = null;
        UUID conversationId = null;

        if (isMutualLike) {
            UserMatchResponse matchResp = userMatchService.create(fromId, toId);
            matchId = matchResp.getId();

            ConversationResponse convResp = conversationService.createOrGet(fromId, toId);
            conversationId = convResp.getId();
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

        return swipeRepository.findByFromUser_IdAndToUser_Id(userA, userB)
                .filter(UserSwipe::isLiked)
                .isPresent()
                &&
                swipeRepository.findByFromUser_IdAndToUser_Id(userB, userA)
                        .filter(UserSwipe::isLiked)
                        .isPresent();
        }
}