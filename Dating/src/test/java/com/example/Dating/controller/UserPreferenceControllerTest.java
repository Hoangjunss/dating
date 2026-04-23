package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.service.UserPreferenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(UserPreferenceController.class)
class UserPreferenceControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserPreferenceService userPreferenceService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void savePreference_Returns200() throws Exception {
        UserPreferenceRequest request = new UserPreferenceRequest();
        request.setGenderPreference("FEMALE");
        request.setMinAge(20);
        request.setMaxAge(30);
        UserPreferenceResponse response = UserPreferenceResponse.builder().genderPreference("FEMALE").build();
        when(userPreferenceService.save(any(UUID.class), any(UserPreferenceRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/preferences/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genderPreference").value("FEMALE"));
    }

    @Test
    @WithMockUser
    void getPreference_Returns200() throws Exception {
        UserPreferenceResponse response = UserPreferenceResponse.builder().genderPreference("MALE").build();
        when(userPreferenceService.get(any(UUID.class))).thenReturn(response);
        mockMvc.perform(get("/api/preferences/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genderPreference").value("MALE"));
    }

    @Test
    @WithMockUser
    void deletePreference_Returns204() throws Exception {
        mockMvc.perform(delete("/api/preferences/me"))
                .andExpect(status().isNoContent());
    }
}