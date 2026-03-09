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
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.model.ast.builder.CollectionRowDeleteByUpdateSetNullBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSwipeServiceImpl implements UserSwipeService {

    private final UserSwipeRepository repository;
    private final UserProfileService userProfileService;
    private final UserMatchService userMatchService;
    private final ConversationService conversationService;

    @Override
    public SwipeResultResponse swipe(SwipeRequest request) {

        if (repository.existsByFromUser_IdAndToUser_Id(
                request.getFromUserId(),
                request.getToUserId())) {
            throw new RuntimeException("Already swiped");
        }

        var entity = UserSwipeMapper.toEntity(request);
        UserProfile fromUser = UserProfileMapper.toEntity(userProfileService.get(request.getFromUserId()));
        UserProfile toUser = UserProfileMapper.toEntity(userProfileService.get(request.getToUserId()));
        entity.setFromUser(fromUser);
        entity.setToUser(toUser);

        repository.save(entity);

        boolean isMutualLike = isMatch(request.getFromUserId(), request.getToUserId());

        UUID matchId = null;
        UUID conversationId = null;

        if (isMutualLike){
            // Tạo match
            UserMatchResponse match = userMatchService.create(request.getFromUserId(), request.getToUserId());
            matchId = match.getId();

            // Tạo conversation
            ConversationCreateRequest conversationCreateRequest =  new ConversationCreateRequest(request.getFromUserId(), request.getToUserId());
            ConversationResponse conv = conversationService.create(conversationCreateRequest); // thêm method này
            conversationId = conv.getId();
        }

        return new SwipeResultResponse(entity.getId(), request.isLiked(), isMutualLike, matchId, conversationId);
    }

    /**
     * A match happens when:
     * A likes B AND B likes A
     */
    @Override
    public boolean isMatch(UUID userA, UUID userB) {

        return repository.findByFromUser_IdAndToUser_Id(userA, userB)
                .filter(UserSwipe::isLiked)
                .isPresent()
                &&
                repository.findByFromUser_IdAndToUser_Id(userB, userA)
                        .filter(UserSwipe::isLiked)
                        .isPresent();
    }
}