package com.example.Dating.repository;

import com.example.Dating.entities.UserSwipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserSwipeRepository extends JpaRepository<UserSwipe, UUID> {

    boolean existsByFromUser_IdAndToUser_Id(UUID from, UUID to);

    @Query("SELECT COUNT(s) = 2 " +
            "FROM UserSwipe s " +
            "WHERE (s.fromUser.userId = :user1 AND s.toUser.userId = :user2 AND s.isLiked = true) " +
            "   OR (s.fromUser.userId = :user2 AND s.toUser.userId = :user1 AND s.isLiked = true)")
    boolean existsMutualLike(@Param("user1") UUID user1, @Param("user2") UUID user2);

    Optional<UserSwipe> findByFromUser_IdAndToUser_Id(UUID fromUserId, UUID toUserId);

}