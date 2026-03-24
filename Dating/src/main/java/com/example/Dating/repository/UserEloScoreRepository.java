package com.example.Dating.repository;

import com.example.Dating.entities.UserEloScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserEloScoreRepository extends JpaRepository<UserEloScore, UUID> {

    /**
     * Atomically increment totalSeen and update score.
     * Called when a user's profile is shown to someone.
     */
    @Modifying
    @Query("""
        UPDATE UserEloScore e
        SET e.totalSeen = e.totalSeen + 1,
            e.score = :newScore
        WHERE e.userId = :userId
    """)
    void updateOnSeen(@Param("userId") UUID userId, @Param("newScore") double newScore);

    /**
     * Atomically increment totalLikes and update score.
     * Called when this user receives a like.
     */
    @Modifying
    @Query("""
        UPDATE UserEloScore e
        SET e.totalSeen = e.totalSeen + 1,
            e.totalLikes = e.totalLikes + 1,
            e.score = :newScore
        WHERE e.userId = :userId
    """)
    void updateOnLike(@Param("userId") UUID userId, @Param("newScore") double newScore);
}