package com.example.Dating.controller;

import com.example.Dating.dtos.request.PostCreateRequest;
import com.example.Dating.dtos.response.PostResponse;
import com.example.Dating.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private PostService postService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createPost_Returns201() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setUserId(UUID.randomUUID());
        request.setContent("Hello world");
        PostResponse response = PostResponse.builder().id(UUID.randomUUID()).content("Hello world").build();
        when(postService.create(any(PostCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello world"));
    }

    @Test
    @WithMockUser
    void getUserPosts_ReturnsPage() throws Exception {
        UUID userId = UUID.randomUUID();
        var page = new PageImpl<>(List.of(PostResponse.builder().id(UUID.randomUUID()).build()));
        when(postService.getUserPosts(eq(userId), anyInt(), anyInt())).thenReturn(page);
        mockMvc.perform(get("/api/posts/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }



    @Test
    @WithMockUser
    void deletePost_Returns204() throws Exception {
        UUID postId = UUID.randomUUID();
        mockMvc.perform(delete("/api/posts/{postId}", postId))
                .andExpect(status().isNoContent());
    }
}