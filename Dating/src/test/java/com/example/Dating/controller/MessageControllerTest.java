package com.example.Dating.controller;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.request.PhotoSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private MessageService messageService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void sendMessage_Returns200() throws Exception {
        MessageSendRequest request = new MessageSendRequest();
        request.setConversationId(UUID.randomUUID());
        request.setContent("Hello");
        MessageResponse response = MessageResponse.builder().id(UUID.randomUUID()).content("Hello").build();
        when(messageService.send(any(MessageSendRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello"));
    }

    @Test
    @WithMockUser
    void sendPhoto_Returns200() throws Exception {
        MockMultipartFile file = new MockMultipartFile("photo", "test.jpg", "image/jpeg", "test image".getBytes());
        mockMvc.perform(multipart("/api/messages/photo")
                        .file(file)
                        .param("conversationId", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getMessages_ReturnsList() throws Exception {
        UUID convId = UUID.randomUUID();
        when(messageService.getMessages(eq(convId), any(UUID.class))).thenReturn(List.of());
        mockMvc.perform(get("/api/messages/{conversationId}", convId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteForMe_Returns204() throws Exception {
        UUID messageId = UUID.randomUUID();
        mockMvc.perform(delete("/api/messages/{messageId}/delete-for-me", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void unsendForEveryone_Returns204() throws Exception {
        UUID messageId = UUID.randomUUID();
        mockMvc.perform(delete("/api/messages/{messageId}/unsend", messageId))
                .andExpect(status().isNoContent());
    }
}