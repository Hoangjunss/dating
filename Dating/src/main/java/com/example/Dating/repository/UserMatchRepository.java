package com.example.Dating.repository;

import com.example.Dating.entities.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMatchRepository extends JpaRepository<UserMatch, UUID> {

    List<UserMatch> findByUserAIdOrUserBId(UUID a, UUID b);

    Optional<UserMatch> findByUserAIdAndUserBId(UUID a, UUID b);
}