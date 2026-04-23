package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.service.UserInterestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
@WebMvcTest(UserInterestController.class)
class UserInterestControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserInterestService userInterestService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createUserInterest_Returns201() throws Exception {
        UserInterestRequest request = new UserInterestRequest();
        request.setInterestId(UUID.randomUUID());
        UserInterestResponse response = UserInterestResponse.builder().id(UUID.randomUUID()).build();
        when(userInterestService.create(any(UserInterestRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/user-interests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void getUserInterests_ReturnsList() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userInterestService.getByUser(userId)).thenReturn(List.of());
        mockMvc.perform(get("/api/user-interests/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getMyInterests_ReturnsList() throws Exception {
        when(userInterestService.getByUser(any(UUID.class))).thenReturn(List.of());
        mockMvc.perform(get("/api/user-interests/me"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteUserInterest_Returns204() throws Exception {
        UUID interestId = UUID.randomUUID();
        mockMvc.perform(delete("/api/user-interests/{interestId}", interestId))
                .andExpect(status().isNoContent());
    }
}