package com.example.Dating.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Bảng lưu "Delete for me".
 *
 * Mỗi row = một người (userId) đã xóa một tin nhắn (messageId) khỏi view của mình.
 * Tin nhắn gốc vẫn tồn tại trong bảng messages — người kia không bị ảnh hưởng.
 *
 * Ưu điểm so với field trên Message:
 *  - Dễ mở rộng: receiver cũng có thể "delete for me" sau này
 *  - Schema Message không thay đổi
 *  - Query rõ ràng: "tin nhắn nào user này đã xóa"
 */
@Entity
@Table(
        name = "message_deletions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"message_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDeletion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    /**
     * UUID của người đã xóa tin nhắn này khỏi view của họ.
     * Không join về UserProfile để giữ đơn giản.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private Instant deletedAt;

    @PrePersist
    void prePersist() {
        deletedAt = Instant.now();
    }
}