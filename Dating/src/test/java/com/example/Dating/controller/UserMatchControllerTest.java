package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.service.UserMatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserMatchController.class)
class UserMatchControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserMatchService userMatchService;

    @Test
    @WithMockUser
    void getActiveMatches_ReturnsList() throws Exception {
        UserMatchResponse match = UserMatchResponse.builder()
                .id(UUID.randomUUID())
                .userAId(UUID.randomUUID())
                .userBId(UUID.randomUUID())
                .active(true)
                .matchedAt(Instant.now())
                .build();
        when(userMatchService.getActiveMatches(any(UUID.class))).thenReturn(List.of(match));

        mockMvc.perform(get("/api/matches/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    @WithMockUser
    void getAllMatches_ReturnsList() throws Exception {
        when(userMatchService.getAllMatches(any(UUID.class))).thenReturn(List.of());
        mockMvc.perform(get("/api/matches/me/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void unmatch_Returns204() throws Exception {
        UUID matchId = UUID.randomUUID();
        mockMvc.perform(delete("/api/matches/{matchId}", matchId))
                .andExpect(status().isNoContent());
        verify(userMatchService).unmatch(eq(matchId), any(UUID.class));
    }
}