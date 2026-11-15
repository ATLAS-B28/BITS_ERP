package com.example.bitserp.modules.automation.controller;

import com.example.bitserp.modules.automation.dto.NotificationResponse;
import com.example.bitserp.modules.automation.entity.Notification;
import com.example.bitserp.modules.automation.service.NotificationService;
import com.example.bitserp.shared.dto.ApiResponse;
import com.example.bitserp.shared.entity.BaseEntity;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll(
            @AuthenticationPrincipal String email
    ) {
        UUID userId = getUserId(email);
        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(notificationService.getAll(userId))
        ));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnread(
            @AuthenticationPrincipal String email
    ) {
        UUID userId = getUserId(email);
        return ResponseEntity.ok(
                ApiResponse.ok(
                        toResponse(notificationService.getUnreadNotifications(userId))
                )
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>>
    getUnreadCount(@AuthenticationPrincipal String email) {
        UUID userId = getUserId(email);
        return ResponseEntity.ok(ApiResponse.ok(
                notificationService.getUnreadCount(userId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable Integer id) {
        notificationService.markRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @AuthenticationPrincipal String email) {
        UUID userId = getUserId(email);
        notificationService.markAllRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("All marked as read", null));
    }

    private UUID getUserId(String email) {
        return userRepository.findByEmail(email)
                .map(BaseEntity::getId)
                .orElseThrow();
    }

    private List<NotificationResponse> toResponse(
            List<Notification> notifications
    ) {
        return notifications.stream().map(n -> new NotificationResponse(
                n.getId(), n.getTitle(), n.getMessage(),
                n.getType(), n.getReferenceType(),
                n.getReferenceId(), n.getIsRead(), n.getCreatedAt()
        )).collect(Collectors.toList());
    }
}
