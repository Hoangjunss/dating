package com.example.Dating.controller;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageWsControllerTest {

    @Mock private MessageService messageService;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @InjectMocks private MessageWsController messageWsController;

    @Test
    void send_ShouldProcessAndBroadcast() {
        Principal principal = () -> UUID.randomUUID().toString();
        MessageSendRequest request = new MessageSendRequest();
        request.setConversationId(UUID.randomUUID());
        request.setContent("Hello WS");
        MessageResponse response = MessageResponse.builder().id(UUID.randomUUID()).content("Hello WS").build();

        when(messageService.send(any(MessageSendRequest.class))).thenReturn(response);

        messageWsController.send(request, principal);

        verify(messageService).send(any(MessageSendRequest.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation." + request.getConversationId()), eq(response));
    }

    @Test
    void unsend_ShouldCallService() {
        Principal principal = () -> UUID.randomUUID().toString();
        MessageWsController.UnsendRequest request = new MessageWsController.UnsendRequest();
        request.setMessageId(UUID.randomUUID());
        request.setConversationId(UUID.randomUUID());

        messageWsController.unsend(request, principal);

        verify(messageService).unsendForEveryone(eq(request.getMessageId()), any(UUID.class));
    }
}