package com.example.Dating.controller;

import com.example.Dating.dtos.request.PostCreateRequest;
import com.example.Dating.dtos.response.PostResponse;
import com.example.Dating.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestBody PostCreateRequest request) {
        log.info("POST /api/posts - Creating post for user: {}", request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getUserPosts(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/posts/user/{} - Fetching posts", userId);
        return ResponseEntity.ok(postService.getUserPosts(userId, page, size));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/posts - Fetching all posts for feed");
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable UUID postId) {
        log.info("DELETE /api/posts/{} - Deleting post", postId);
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}