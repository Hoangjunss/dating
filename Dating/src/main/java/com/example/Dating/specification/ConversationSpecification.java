package com.example.Dating.specification;

import com.example.Dating.entities.Conversation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ConversationSpecification {

    public static Specification<Conversation> findUserConversations(UUID userId) {
        return (Root<Conversation> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            // 1. Sắp xếp theo thời gian hoạt động mới nhất (lastActivityAt) giảm dần
            query.orderBy(cb.desc(root.get("lastActivityAt")));

            // 2. Lọc: User phải là userA HOẶC userB
            return cb.or(
                    cb.equal(root.get("userA").get("id"), userId),
                    cb.equal(root.get("userB").get("id"), userId)
            );
        };
    }
}