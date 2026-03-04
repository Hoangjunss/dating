package com.example.Dating.service;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResponse;
import com.example.Dating.entities.UserSwipe;
import com.example.Dating.mapper.UserSwipeMapper;
import com.example.Dating.repository.UserSwipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSwipeServiceImpl implements UserSwipeService {

    private final UserSwipeRepository repository;

    @Override
    public SwipeResponse swipe(SwipeRequest request) {

        if (repository.existsByFromUserIdAndToUserId(
                request.getFromUserId(),
                request.getToUserId())) {
            throw new RuntimeException("Already swiped");
        }

        var entity = UserSwipeMapper.toEntity(request);

        repository.save(entity);

        return UserSwipeMapper.toResponse(entity);
    }

    /**
     * A match happens when:
     * A likes B AND B likes A
     */
    @Override
    public boolean isMatch(UUID userA, UUID userB) {

        return repository.findByFromUserIdAndToUserId(userA, userB)
                .filter(UserSwipe::isLiked)
                .isPresent()
                &&
                repository.findByFromUserIdAndToUserId(userB, userA)
                        .filter(UserSwipe::isLiked)
                        .isPresent();
    }
}