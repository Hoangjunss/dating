package com.example.Dating.repository;

import com.example.Dating.entities.MessageDeletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface MessageDeletionRepository extends JpaRepository<MessageDeletion, UUID> {

    boolean existsByMessage_IdAndUserId(UUID messageId, UUID userId);

    @Query("""
            SELECT md.message.id
            FROM MessageDeletion md
            WHERE md.userId = :userId
              AND md.message.conversation.id = :conversationId
            """)
    Set<UUID> findDeletedMessageIdsByUserInConversation(
            @Param("userId") UUID userId,
            @Param("conversationId") UUID conversationId);
}