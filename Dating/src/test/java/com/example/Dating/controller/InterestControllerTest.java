package com.example.Dating.controller;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;
import com.example.Dating.service.InterestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
@WebMvcTest(InterestController.class)
class InterestControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private InterestService interestService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createInterest_Returns201() throws Exception {
        InterestCreateRequest request = new InterestCreateRequest();
        request.setName("Music");
        InterestResponse response = InterestResponse.builder().id(UUID.randomUUID()).name("Music").build();
        when(interestService.create(any(InterestCreateRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/interests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Music"));
    }

    @Test
    @WithMockUser
    void getInterest_Returns200() throws Exception {
        UUID id = UUID.randomUUID();
        InterestResponse response = InterestResponse.builder().id(id).name("Sport").build();
        when(interestService.get(id)).thenReturn(response);
        mockMvc.perform(get("/api/interests/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sport"));
    }

    @Test
    @WithMockUser
    void getAllInterests_ReturnsList() throws Exception {
        when(interestService.getAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/interests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteInterest_Returns204() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/interests/{id}", id))
                .andExpect(status().isNoContent());
    }
}