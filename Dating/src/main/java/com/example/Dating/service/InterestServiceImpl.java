package com.example.Dating.service;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;
import com.example.Dating.entities.Interest;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.InterestMapper;
import com.example.Dating.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Interest CRUD operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InterestServiceImpl implements InterestService {

    private final InterestRepository repository;

    /**
     * Creates a new interest.
     * Business rule: Interest name must be unique.
     */
    @Override
    @Transactional
    public InterestResponse create(InterestCreateRequest request) {
        log.info("Creating interest with name: {}", request.getName());

        if (repository.existsByName(request.getName())) {
            log.warn("Interest already exists with name: {}", request.getName());
            throw new DuplicateResourceException("Interest with name '" + request.getName() + "' already exists");
        }

        Interest entity = InterestMapper.toEntity(request);
        repository.save(entity);

        log.info("Interest created successfully with id: {}", entity.getId());
        return InterestMapper.toResponse(entity);
    }

    /**
     * Retrieves an interest by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public InterestResponse get(UUID id) {
        log.debug("Fetching interest with id: {}", id);
        
        return repository.findById(id)
                .map(InterestMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Interest not found with id: {}", id);
                    return new ResourceNotFoundException("Interest not found with id: " + id);
                });
    }

    /**
     * Retrieves all interests.
     */
    @Override
    @Transactional(readOnly = true)
    public List<InterestResponse> getAll() {
        log.debug("Fetching all interests");
        
        return repository.findAll()
                .stream()
                .map(InterestMapper::toResponse)
                .toList();
    }

    /**
     * Deletes an interest by ID.
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting interest with id: {}", id);
        
        if (!repository.existsById(id)) {
            log.warn("Interest not found for deletion with id: {}", id);
            throw new ResourceNotFoundException("Interest not found with id: " + id);
        }
        
        repository.deleteById(id);
        log.info("Interest deleted successfully with id: {}", id);
    }
}