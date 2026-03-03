package com.example.Dating.service;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.mapper.UserInterestMapper;
import com.example.Dating.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInterestServiceImpl implements UserInterestService {

    private final UserInterestRepository repository;

    @Override
    public UserInterestResponse add(UserInterestRequest r) {

        if (repository.existsByUserIdAndInterestId(r.getUserId(), r.getInterestId())) {
            throw new RuntimeException("Already added");
        }

        var entity = UserInterestMapper.toEntity(r);

        repository.save(entity);

        return UserInterestMapper.toResponse(entity);
    }

    @Override
    public List<UserInterestResponse> getByUser(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(UserInterestMapper::toResponse)
                .toList();
    }

    @Override
    public void remove(UUID userId, UUID interestId) {
        repository.deleteByUserIdAndInterestId(userId, interestId);
    }
}