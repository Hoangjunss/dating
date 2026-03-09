package com.example.Dating.service;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResponse;
import com.example.Dating.dtos.response.SwipeResultResponse;

import java.util.UUID;

public interface UserSwipeService {

    SwipeResultResponse swipe(SwipeRequest request);

    boolean isMatch(UUID userA, UUID userB);
}