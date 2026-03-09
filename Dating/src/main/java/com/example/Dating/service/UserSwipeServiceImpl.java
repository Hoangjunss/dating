package com.example.Dating.service;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResponse;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.entities.UserSwipe;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.mapper.UserSwipeMapper;
import com.example.Dating.repository.UserSwipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSwipeServiceImpl implements UserSwipeService {

    private final UserSwipeRepository repository;
    private final UserProfileService userProfileService;

    @Override
    public SwipeResponse swipe(SwipeRequest request) {

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

        return UserSwipeMapper.toResponse(entity);
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