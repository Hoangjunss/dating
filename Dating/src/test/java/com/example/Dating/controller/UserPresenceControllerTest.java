package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.service.UserPresenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPresenceController.class)
class UserPresenceControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserPresenceService userPresenceService;

    @Test
    @WithMockUser
    void setOnline_Returns200() throws Exception {
        mockMvc.perform(post("/api/presence/me/online"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void setOffline_Returns200() throws Exception {
        mockMvc.perform(post("/api/presence/me/offline"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getPresence_Returns200() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPresenceResponse response = UserPresenceResponse.builder().userId(userId).online(true).build();
        when(userPresenceService.get(userId)).thenReturn(response);
        mockMvc.perform(get("/api/presence/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @WithMockUser
    void deletePresence_Returns204() throws Exception {
        mockMvc.perform(delete("/api/presence/me"))
                .andExpect(status().isNoContent());
    }
}