package com.example.Dating.controller;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping
    public MessageResponse send(@RequestBody MessageSendRequest request) {
        return service.send(request);
    }

    @GetMapping("/{conversationId}")
    public List<MessageResponse> getMessages(@PathVariable UUID conversationId) {
        return service.getMessages(conversationId);
    }
}