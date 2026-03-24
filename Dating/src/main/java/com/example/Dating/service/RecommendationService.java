package com.example.Dating.service;

import com.example.Dating.dtos.response.CandidateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RecommendationService {

    /**
     * Returns a paginated list of candidate profiles for the given user.
     *
     * Pipeline:
     *  1. Hard filter  — preference constraints (gender/age/distance) + exclude already-swiped
     *  2. Score        — composite score per candidate
     *  3. Sort         — highest score first
     *  4. Diversity    — prevent too many identical candidates back-to-back
     *  5. Paginate     — return requested page
     */
    Page<CandidateResponse> getCandidates(UUID userId, Pageable pageable);

    void processSwipeElo(UUID fromId, UUID toId, boolean liked);
}