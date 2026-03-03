package com.example.Dating.repository;

import com.example.Dating.entities.UserSwipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSwipeRepository extends JpaRepository<UserSwipe, UUID> {

    boolean existsByFromUserIdAndToUserId(UUID from, UUID to);

    Optional<UserSwipe> findByFromUserIdAndToUserId(UUID from, UUID to);
}