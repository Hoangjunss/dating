package com.example.Dating.service;

import com.example.Dating.dtos.request.PostCreateRequest;
import com.example.Dating.dtos.response.PostResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PostService {
    PostResponse create(PostCreateRequest request);
    Page<PostResponse> getUserPosts(UUID userId, int page, int size);
    Page<PostResponse> getAllPosts(int page, int size);
    void delete(UUID postId);
}