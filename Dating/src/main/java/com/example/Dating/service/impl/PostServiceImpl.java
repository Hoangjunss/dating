package com.example.Dating.service.impl;

import com.example.Dating.dtos.request.PostCreateRequest;
import com.example.Dating.dtos.response.PostResponse;
import com.example.Dating.entities.Post;
import com.example.Dating.entities.User;
import com.example.Dating.repository.PostRepository;
import com.example.Dating.service.AuthService;
import com.example.Dating.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// Implementation
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final AuthService userService; // Giống cách bạn dùng trong ConversationServiceImpl

    @Override
    @Transactional
    public PostResponse create(PostCreateRequest request) {
        User user = userService.findById(request.getUserId());

        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();

        post = postRepository.save(post);
        return toResponse(post);
    }

    @Override
    public Page<PostResponse> getUserPosts(UUID userId, int page, int size) {
        return postRepository.findByUserUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Override
    public Page<PostResponse> getAllPosts(int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(UUID postId) {
        postRepository.deleteById(postId);
    }

    // Mapper nội bộ giống ConversationMapper của bạn
    private PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getUserId())
                .fullName(post.getUser().getUsername())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }
}