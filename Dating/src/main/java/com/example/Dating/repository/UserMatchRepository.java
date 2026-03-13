package com.example.Dating.repository;

import com.example.Dating.entities.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMatchRepository extends JpaRepository<UserMatch, UUID> {

    List<UserMatch> findAllByUserA_UserIdAndUserB_UserId(UUID a, UUID b);

    Optional<UserMatch> findByUserA_UserIdAndUserB_UserId(UUID a, UUID b);

    boolean hasActiveMatchByUserA_UserIdAndUserB_UserId(UUID a, UUID b);

    @Query("SELECT m FROM UserMatch m " +
            "WHERE (m.userA.userId = :userId OR m.userB.userId = :userId) " +
            "AND m.active = true")
    List<UserMatch> findActiveMatchesByUserId(@Param("userId") UUID userId);

    @Query("SELECT m FROM UserMatch m " +
            "WHERE (m.userA.userId = :userId OR m.userB.userId = :userId)")
    List<UserMatch> findAllByUserId(@Param("userId") UUID userId);
}