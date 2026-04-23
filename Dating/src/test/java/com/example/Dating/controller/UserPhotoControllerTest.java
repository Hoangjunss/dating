package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.service.UserPhotoService;
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

@WebMvcTest(UserPhotoController.class)
class UserPhotoControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserPhotoService userPhotoService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createPhoto_Returns201() throws Exception {
        UserPhotoCreateRequest request = new UserPhotoCreateRequest();
        request.setUserId(UUID.randomUUID());
        request.setIsPrimary(true);
        UserPhotoResponse response = UserPhotoResponse.builder().id(UUID.randomUUID()).url("http://image.url").build();
        when(userPhotoService.create(any(UserPhotoCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").value("http://image.url"));
    }

    @Test
    @WithMockUser
    void getPhoto_Returns200() throws Exception {
        UUID photoId = UUID.randomUUID();
        UserPhotoResponse response = UserPhotoResponse.builder().id(photoId).url("url").build();
        when(userPhotoService.get(photoId)).thenReturn(response);
        mockMvc.perform(get("/api/photos/{id}", photoId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getUserPhotos_ReturnsList() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userPhotoService.getByUser(userId)).thenReturn(List.of());
        mockMvc.perform(get("/api/photos/user/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deletePhoto_Returns204() throws Exception {
        UUID photoId = UUID.randomUUID();
        mockMvc.perform(delete("/api/photos/{id}", photoId))
                .andExpect(status().isNoContent());
    }
}