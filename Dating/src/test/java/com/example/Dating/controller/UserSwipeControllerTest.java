package com.example.Dating.controller;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResultResponse;
import com.example.Dating.service.UserSwipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSwipeController.class)
class UserSwipeControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserSwipeService userSwipeService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void swipe_ValidRequest_Returns200() throws Exception {
        SwipeRequest request = new SwipeRequest();
        request.setToUserId(UUID.randomUUID());
        request.setLiked(true);

        SwipeResultResponse response = SwipeResultResponse.builder()
                .id(UUID.randomUUID())
                .isLiked(true)
                .isMutualLike(false)
                .build();

        when(userSwipeService.swipe(any(SwipeRequest.class), any(UUID.class))).thenReturn(response);

        mockMvc.perform(post("/api/swipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true));
    }


}