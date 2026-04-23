package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserProfileService userProfileService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createProfile_Valid_Returns201() throws Exception {
        UUID userId = UUID.randomUUID();
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setUserId(userId);
        request.setDisplayName("John Doe");
        request.setGender("MALE");
        request.setBirthday(LocalDate.of(1995, 1, 1));

        UserProfileResponse response = UserProfileResponse.builder()
                .userId(userId)
                .displayName("John Doe")
                .build();

        when(userProfileService.create(any(UserProfileCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void getProfile_Returns200() throws Exception {
        UUID userId = UUID.randomUUID();
        UserProfileResponse response = UserProfileResponse.builder().userId(userId).displayName("John").build();
        when(userProfileService.get(userId)).thenReturn(response);

        mockMvc.perform(get("/api/profiles/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("John"));
    }

    @Test
    @WithMockUser
    void getProfilesPaginated_ReturnsPage() throws Exception {
        UUID userId = UUID.randomUUID();
        var page = new PageImpl<>(List.of(UserProfileResponse.builder().userId(UUID.randomUUID()).build()));
        when(userProfileService.getAllPaginated(eq(userId), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/profiles/me/paginated").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void updateProfile_Returns200() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setDisplayName("Updated Name");
        UserProfileResponse response = UserProfileResponse.builder().displayName("Updated Name").build();
        when(userProfileService.update(any(UUID.class), any(UserProfileUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Updated Name"));
    }

    @Test
    @WithMockUser
    void deleteProfile_Returns204() throws Exception {
        mockMvc.perform(delete("/api/profiles/me"))
                .andExpect(status().isNoContent());
    }
}