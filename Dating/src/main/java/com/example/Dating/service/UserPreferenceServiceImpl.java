package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.mapper.UserPreferenceMapper;
import com.example.Dating.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceRepository repository;

    @Override
    public UserPreferenceResponse save(UUID userId, UserPreferenceRequest r) {

        var entity = repository.findById(userId)
                .orElse(UserPreferenceMapper.toEntity(userId, r));

        UserPreferenceMapper.update(entity, r);

        repository.save(entity);

        return UserPreferenceMapper.toResponse(entity);
    }

    @Override
    public UserPreferenceResponse get(UUID userId) {
        return repository.findById(userId)
                .map(UserPreferenceMapper::toResponse)
                .orElse(null);
    }
}