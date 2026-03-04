package com.example.Dating.service;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.entities.Message;
import com.example.Dating.mapper.MessageMapper;
import com.example.Dating.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;

    @Override
    public MessageResponse send(MessageSendRequest request) {

        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(request.getSenderId())
                .content(request.getContent())
                .build();

        repository.save(message);

        return MessageMapper.toResponse(message);
    }

    @Override
    public List<MessageResponse> getMessages(UUID conversationId) {
        return repository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(MessageMapper::toResponse)
                .toList();
    }
}