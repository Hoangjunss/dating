package com.example.Dating.service;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.request.PhotoSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.mapper.MessageMapper;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponse send(MessageSendRequest request);

    MessageResponse sendPhoto(PhotoSendRequest request);

    List<MessageResponse> getMessages(UUID conversationId);

    List<MessageResponse> getMessages(UUID conversationId, UUID viewerId);

    void deleteForMe(UUID messageId, UUID requesterId);

    void unsendForEveryone(UUID messageId, UUID requesterId);



}

