package com.example.Dating.service;

import com.example.Dating.dtos.request.PostCreateRequest;
import com.example.Dating.dtos.response.PostResponse;
import com.example.Dating.entities.Post;
import com.example.Dating.entities.User;
import com.example.Dating.repository.PostRepository;
import com.example.Dating.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock private PostRepository postRepository;
    @Mock private AuthService userService;

    @InjectMocks
    private PostServiceImpl postService;

    private UUID userId;
    private UUID postId;
    private User user;
    private Post post;
    private PostCreateRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        postId = UUID.randomUUID();
        user = User.builder().userId(userId).username("testuser").build();
        post = Post.builder()
                .id(postId)
                .user(user)
                .content("Hello world")
                .imageUrl("http://image.jpg")
                .createdAt(LocalDateTime.now())
                .build();
        request = new PostCreateRequest();
        request.setUserId(userId);
        request.setContent("Hello world");
        request.setImageUrl("http://image.jpg");
    }

    @Test
    void create_Success() {
        when(userService.findById(userId)).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse response = postService.create(request);

        assertThat(response.getId()).isEqualTo(postId);
        assertThat(response.getContent()).isEqualTo("Hello world");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getUserPosts_ShouldReturnPage() {
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findByUserUserIdOrderByCreatedAtDesc(eq(userId), any(PageRequest.class)))
                .thenReturn(postPage);

        Page<PostResponse> result = postService.getUserPosts(userId, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Hello world");
    }

    @Test
    void getAllPosts_ShouldReturnPage() {
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findAllByOrderByCreatedAtDesc(any(PageRequest.class)))
                .thenReturn(postPage);

        Page<PostResponse> result = postService.getAllPosts(0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void delete_Success() {
        doNothing().when(postRepository).deleteById(postId);
        postService.delete(postId);
        verify(postRepository).deleteById(postId);
    }
}