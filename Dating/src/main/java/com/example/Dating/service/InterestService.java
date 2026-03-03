package com.example.Dating.service;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;

import java.util.List;
import java.util.UUID;

public interface InterestService {

    InterestResponse create(InterestCreateRequest r);

    List<InterestResponse> getAll();

    void delete(UUID id);
}