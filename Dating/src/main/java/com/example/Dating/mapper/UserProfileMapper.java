package com.example.Dating.mapper;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.entities.UserProfile;

/**
 * Mapper utility class responsible for converting
 * between UserProfile entity and DTO objects.
 *
 * Keeps mapping logic out of service layer
 * to maintain clean separation of concerns.
 */
public final class UserProfileMapper {

    private UserProfileMapper() {}

    /**
     * Converts create request DTO to entity.
     */
    public static UserProfile toEntity(UserProfileCreateRequest req) {
        return UserProfile.builder()
                .userId(req.getUserId())
                .displayName(req.getDisplayName())
                .gender(req.getGender())
                .birthday(req.getBirthday())
                .bio(req.getBio())
                .heightCm(req.getHeightCm())
                .job(req.getJob())
                .education(req.getEducation())
                .city(req.getCity())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .build();
    }

    /**
     * Converts entity to response DTO.
     * Used to hide internal fields and expose only safe data.
     */
    public static UserProfileResponse toResponse(UserProfile e) {
        return UserProfileResponse.builder()
                .userId(e.getUserId())
                .displayName(e.getDisplayName())
                .gender(e.getGender())
                .birthday(e.getBirthday())
                .bio(e.getBio())
                .heightCm(e.getHeightCm() == null ? null : e.getHeightCm().toString())
                .job(e.getJob())
                .education(e.getEducation())
                .city(e.getCity())
                .verified(e.getVerified())
                .build();
    }

    /**
     * Applies partial update from request to existing entity.
     * Only non-null values will overwrite current data.
     * Prevents accidental data loss.
     */
    public static void updateEntity(UserProfile e, UserProfileUpdateRequest req) {

        if (req.getDisplayName() != null) e.setDisplayName(req.getDisplayName());
        if (req.getBio() != null) e.setBio(req.getBio());
        if (req.getJob() != null) e.setJob(req.getJob());
        if (req.getEducation() != null) e.setEducation(req.getEducation());
        if (req.getCity() != null) e.setCity(req.getCity());
        if (req.getLatitude() != null) e.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) e.setLongitude(req.getLongitude());
    }
}