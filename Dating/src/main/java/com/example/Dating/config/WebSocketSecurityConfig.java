package com.example.Dating.config;

import com.example.Dating.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import java.util.List;
import java.util.UUID;

/**
 * WebSocket Security Config.
 *
 * Client gửi JWT trong STOMP CONNECT header:
 *   CONNECT
 *   Authorization: Bearer <token>
 *
 * Server validate JWT trước khi cho phép connect.
 * Nếu không hợp lệ → disconnect ngay lập tức.
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        // User-specific queue: /user/{userId}/queue/...
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // Chỉ cho phép origin cụ thể, không dùng wildcard *
                .setAllowedOriginPatterns(
                        "http://localhost:5173",
                        "http://localhost:3000"
                )
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
                    return message;
                }

                String token = resolveToken(accessor);

                if (!StringUtils.hasText(token)) {
                    log.warn("WebSocket CONNECT rejected - Missing Authorization header");
                    throw new IllegalArgumentException("Missing JWT token");
                }

                if (!tokenProvider.validateToken(token) || !tokenProvider.isAccessToken(token)) {
                    log.warn("WebSocket CONNECT rejected - Invalid or expired ACCESS token");
                    throw new IllegalArgumentException("Invalid or expired JWT token");
                }

                UUID userId = tokenProvider.getUserIdFromToken(token);
                String username = tokenProvider.getUsernameFromToken(token);

                // Tạo authorities (hiện tại chỉ ROLE_USER)
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                );

                // Principal = userId (UUID) để nhất quán với toàn bộ project
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,           // Principal là UUID
                                null,
                                authorities
                        );

                accessor.setUser(auth);

                log.info("WebSocket CONNECT authenticated successfully - userId: {}, username: {}",
                        userId, username);

                return message;
            }
        });
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String bearer = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
