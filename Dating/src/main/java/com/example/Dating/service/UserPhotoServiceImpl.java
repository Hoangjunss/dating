package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.mapper.UserPhotoMapper;
import com.example.Dating.repository.UserPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPhotoServiceImpl implements UserPhotoService {

    private final UserPhotoRepository repository;

    @Override
    public UserPhotoResponse create(UserPhotoCreateRequest r) {
        var e = UserPhotoMapper.toEntity(r);
        repository.save(e);
        return UserPhotoMapper.toResponse(e);
    }

    @Override
    public List<UserPhotoResponse> getByUser(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(UserPhotoMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}