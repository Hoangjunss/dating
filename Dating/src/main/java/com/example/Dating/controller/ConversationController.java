package com.example.Dating.controller;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService service;

    @PostMapping
    public ConversationResponse create(@RequestBody ConversationCreateRequest request) {
        return service.create(request);
    }

    @GetMapping("/{userId}")
    public List<ConversationResponse> getUserConversations(@PathVariable UUID userId) {
        return service.getUserConversations(userId);
    }
}