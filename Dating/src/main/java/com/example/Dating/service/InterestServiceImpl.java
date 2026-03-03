package com.example.Dating.service;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;
import com.example.Dating.mapper.InterestMapper;
import com.example.Dating.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final InterestRepository repository;

    @Override
    public InterestResponse create(InterestCreateRequest r) {

        if (repository.existsByName(r.getName())) {
            throw new RuntimeException("Interest already exists");
        }

        var entity = InterestMapper.toEntity(r);

        repository.save(entity);

        return InterestMapper.toResponse(entity);
    }

    @Override
    public List<InterestResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(InterestMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}