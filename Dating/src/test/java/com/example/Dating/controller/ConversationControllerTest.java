package com.example.Dating.controller;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.service.ConversationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ConversationService conversationService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getMyConversationsPaginated_ReturnsPage() throws Exception {
        var page = new PageImpl<>(List.of(ConversationResponse.builder().id(UUID.randomUUID()).build()));
        when(conversationService.getUserConversations(any(UUID.class), anyInt(), anyInt())).thenReturn(page);
        mockMvc.perform(get("/api/conversations/me/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void createConversation_Returns201() throws Exception {
        ConversationCreateRequest request = new ConversationCreateRequest();
        request.setUserBId(UUID.randomUUID());
        ConversationResponse response = ConversationResponse.builder().id(UUID.randomUUID()).build();
        when(conversationService.create(any(ConversationCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser
    void getMyConversations_ReturnsList() throws Exception {
        when(conversationService.getUserConversations(any(UUID.class))).thenReturn(List.of());
        mockMvc.perform(get("/api/conversations/me"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getConversationById_Returns200() throws Exception {
        UUID convId = UUID.randomUUID();
        ConversationResponse response = ConversationResponse.builder().id(convId).build();
        when(conversationService.findById(eq(convId), any(UUID.class))).thenReturn(response);
        mockMvc.perform(get("/api/conversations/detail/{conversationId}", convId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(convId.toString()));
    }
}