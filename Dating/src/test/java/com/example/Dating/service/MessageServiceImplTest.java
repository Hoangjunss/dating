package com.example.Dating.service;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.entities.*;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.policy.UnsendPolicy;
import com.example.Dating.repository.ConversationRepository;
import com.example.Dating.repository.MessageDeletionRepository;
import com.example.Dating.repository.MessageRepository;
import com.example.Dating.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock private MessageRepository messageRepository;
    @Mock private MessageDeletionRepository deletionRepository;
    @Mock private ConversationRepository conversationRepository;
    @Mock private UserMatchService userMatchService;
    @Mock private AuthService userService;
    @Mock private MessagePhotoService messagePhotoService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private UnsendPolicy unsendPolicy;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private UUID conversationId, senderId, messageId;
    private Conversation conversation;
    private User sender;
    private Message message;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        User receiver = User.builder().userId(UUID.randomUUID()).build();
        sender = User.builder().userId(senderId).build();
        conversation = Conversation.builder()
                .id(conversationId)
                .userA(sender)
                .userB(receiver)
                .build();
        message = Message.builder()
                .id(messageId)
                .sender(sender)
                .conversation(conversation)
                .type(MessageType.TEXT)
                .content("Hello")
                .unsent(false)
                .sentAt(Instant.now())
                .build();
    }

    @Test
    void send_Success() {
        MessageSendRequest request = new MessageSendRequest();
        request.setConversationId(conversationId);
        request.setSenderId(senderId);
        request.setContent("Hi");

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(userService.findById(senderId)).thenReturn(sender);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        MessageResponse response = messageService.send(request);
        assertThat(response.getContent()).isEqualTo("Hello");
        verify(notificationService).createAndPush(any(), any(), any(), any(), any(), any());
    }

    @Test
    void unsendForEveryone_Success() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        doNothing().when(unsendPolicy).validate(message);
        messageService.unsendForEveryone(messageId, senderId);
        verify(unsendPolicy).validate(message);
        verify(messageRepository).save(message);
        assertThat(message.getUnsent()).isTrue();
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void unsendForEveryone_NotSender_Throws() {
        UUID otherId = UUID.randomUUID();
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        assertThatThrownBy(() -> messageService.unsendForEveryone(messageId, otherId))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deleteForMe_Success() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(deletionRepository.existsByMessage_IdAndUserId(messageId, senderId)).thenReturn(false);
        messageService.deleteForMe(messageId, senderId);
        verify(deletionRepository).save(any(MessageDeletion.class));
    }

    @Test
    void deleteForMe_AlreadyDeleted_Throws() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(deletionRepository.existsByMessage_IdAndUserId(messageId, senderId)).thenReturn(true);
        assertThatThrownBy(() -> messageService.deleteForMe(messageId, senderId))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getMessages_FiltersDeletedIds() {
        UUID viewerId = senderId;
        when(messageRepository.findByConversationIdOrderBySentAtAsc(conversationId))
                .thenReturn(List.of(message));
        when(deletionRepository.findDeletedMessageIdsByUserInConversation(viewerId, conversationId))
                .thenReturn(Set.of(messageId)); // message deleted for viewer
        List<MessageResponse> messages = messageService.getMessages(conversationId, viewerId);
        assertThat(messages).isEmpty();
    }
}