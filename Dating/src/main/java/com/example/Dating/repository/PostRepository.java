package com.example.Dating.repository;

import com.example.Dating.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    // Lấy bài đăng của một user cụ thể (hiển thị trên Profile)
    Page<Post> findByUserUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // Lấy tất cả bài đăng (cho Newfeed)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}