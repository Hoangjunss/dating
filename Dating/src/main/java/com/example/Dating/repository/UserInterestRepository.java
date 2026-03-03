package com.example.Dating.repository;

import com.example.Dating.entities.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserInterestRepository
        extends JpaRepository<UserInterest, UUID> {

    List<UserInterest> findByUserId(UUID userId);

    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);

    void deleteByUserIdAndInterestId(UUID userId, UUID interestId);
}