package com.example.Dating.repository;

import com.example.Dating.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository
        extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByUserAIdAndUserBId(UUID a, UUID b);

    List<Conversation> findByUserAIdOrUserBId(UUID a, UUID b);
}