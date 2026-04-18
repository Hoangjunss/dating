package com.example.Dating.controller;

import com.example.Dating.dtos.response.NotificationResponse;
import com.example.Dating.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> list(
            Authentication auth,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(notificationService.listForUser(userId, pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        long n = notificationService.countUnread(userId);
        return ResponseEntity.ok(Map.of("count", n));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(
            @PathVariable UUID id,
            Authentication auth
    ) {
        UUID userId = (UUID) auth.getPrincipal();
        notificationService.markRead(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        notificationService.markAllRead(userId);
        return ResponseEntity.noContent().build();
    }
}
