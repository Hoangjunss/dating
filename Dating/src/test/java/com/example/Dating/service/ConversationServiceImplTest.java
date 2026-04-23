package com.example.Dating.service;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.entities.Conversation;
import com.example.Dating.entities.User;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.ConversationRepository;
import com.example.Dating.service.impl.ConversationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceImplTest {

    @Mock private ConversationRepository repository;
    @Mock private UserMatchService userMatchService;
    @Mock private AuthService userService;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private UUID userAId, userBId;
    private User userA, userB;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        userAId = UUID.randomUUID();
        userBId = UUID.randomUUID();
        userA = User.builder().userId(userAId).build();
        userB = User.builder().userId(userBId).build();
        conversation = Conversation.builder()
                .id(UUID.randomUUID())
                .userA(userA)
                .userB(userB)
                .build();
    }

    @Test
    void create_Success() {
        ConversationCreateRequest request = new ConversationCreateRequest();
        request.setUserAId(userAId);
        request.setUserBId(userBId);
        when(userMatchService.hasActiveMatch(userAId, userBId)).thenReturn(true);
        when(userService.findById(userAId)).thenReturn(userA);
        when(userService.findById(userBId)).thenReturn(userB);
        when(repository.findByUserA_UserIdAndUserB_UserId(any(), any())).thenReturn(Optional.empty());
        when(repository.save(any(Conversation.class))).thenReturn(conversation);

        ConversationResponse response = conversationService.create(request);
        assertThat(response.getId()).isEqualTo(conversation.getId());
    }

    @Test
    void create_NoActiveMatch_Throws() {
        ConversationCreateRequest request = new ConversationCreateRequest();
        request.setUserAId(userAId);
        request.setUserBId(userBId);
        when(userMatchService.hasActiveMatch(userAId, userBId)).thenReturn(false);

        assertThatThrownBy(() -> conversationService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findById_UserIsMember_ReturnsResponse() {
        when(repository.findById(conversation.getId())).thenReturn(Optional.of(conversation));
        ConversationResponse response = conversationService.findById(conversation.getId(), userAId);
        assertThat(response.getId()).isEqualTo(conversation.getId());
    }

    @Test
    void findById_UserNotMember_Throws() {
        when(repository.findById(conversation.getId())).thenReturn(Optional.of(conversation));
        assertThatThrownBy(() -> conversationService.findById(conversation.getId(), UUID.randomUUID()))
                .isInstanceOf(ValidationException.class);
    }
}