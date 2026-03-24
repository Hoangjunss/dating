package com.example.Dating.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.*;

/**
 * Represents a private chat room between two matched users.
 * One conversation per match.
 */
@Entity
@Table(
        name = "conversations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_a_id", "user_b_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a_id")
    private User userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b_id")
    private User userB;

    private String nicknameA;

    private String nicknameB;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    @OrderBy("sentAt DESC ")
    private List<Message> messages = new ArrayList<>();

    private Instant createdAt;
    private Instant lastActivityAt;

    @PrePersist
    void prePersist() {
        nicknameA = userA.getUsername();
        nicknameB = userB.getUsername();
        createdAt = Instant.now();
        lastActivityAt=Instant.now();
    }

    public String nickName(UUID selfId) {
        if (userA.getUserId().equals(selfId))
            return nicknameB;
        else return nicknameA;
    }

}