package com.example.Dating.repository;

import com.example.Dating.entities.UserSwipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserSwipeRepository extends JpaRepository<UserSwipe, UUID> {

    boolean existsByFromUser_UserIdAndToUser_UserId(UUID from, UUID to);

    boolean existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue(UUID fromUserId, UUID toUserId);

}