package com.flowermarket.controller;

import com.flowermarket.entity.Notification;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> get(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getForUser(principal.getUser().getId()));
    }

    // Phase 13: lightweight feed for the navbar bell dropdown.
    @GetMapping("/recent")
    public ResponseEntity<List<Notification>> getRecent(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getRecentForUser(principal.getUser().getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(principal.getUser().getId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markRead(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        notificationService.markRead(principal.getUser(), id);
        return ResponseEntity.ok(Map.of("message", "Marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllRead(principal.getUser());
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
}
