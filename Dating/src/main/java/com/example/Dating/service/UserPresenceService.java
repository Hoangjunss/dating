package com.example.Dating.service;

import com.example.Dating.dtos.response.UserPresenceResponse;

import java.util.UUID;

public interface UserPresenceService {

    /**
     * Updates user presence to online.
     */
    void setOnline(UUID userId);

    /**
     * Updates user presence to offline.
     */
    void setOffline(UUID userId);

    /**
     * Retrieves user presence status.
     */
    UserPresenceResponse get(UUID userId);

    /**
     * Deletes user presence data.
     */
    void delete(UUID userId);
}