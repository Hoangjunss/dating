package com.example.Dating.controller;

import com.example.Dating.dtos.response.NotificationResponse;
import com.example.Dating.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private NotificationService notificationService;

    @Test
    @WithMockUser
    void listNotifications_ReturnsPage() throws Exception {
        var page = new PageImpl<>(List.of(NotificationResponse.builder().id(UUID.randomUUID()).title("Test").build()));
        when(notificationService.listForUser(any(UUID.class), any(PageRequest.class))).thenReturn(page);
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test"));
    }

    @Test
    @WithMockUser
    void unreadCount_ReturnsCount() throws Exception {
        when(notificationService.countUnread(any(UUID.class))).thenReturn(5L);
        mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    @WithMockUser
    void markRead_Returns204() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch("/api/notifications/{id}/read", id))
                .andExpect(status().isNoContent());
        verify(notificationService).markRead(eq(id), any(UUID.class));
    }

    @Test
    @WithMockUser
    void markAllRead_Returns204() throws Exception {
        mockMvc.perform(post("/api/notifications/read-all"))
                .andExpect(status().isNoContent());
        verify(notificationService).markAllRead(any(UUID.class));
    }
}