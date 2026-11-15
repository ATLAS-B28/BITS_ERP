package com.example.bitserp.modules.automation.service;

import com.example.bitserp.modules.automation.entity.Notification;
import com.example.bitserp.modules.automation.repository.NotificationRepository;
import com.example.bitserp.shared.entity.User;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void notifyUser(UUID userId, String title, String message, String type,
    String referenceType, UUID referenceID) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) { return; }
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceID);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyByRoles(List<String> roles, String title, String message,
                              String type, String referenceType, UUID referenceID) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> roles.contains(u.getRole().getName())
                && u.getActive())
                .toList();

        users.forEach(user -> {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setReferenceType(referenceType);
            notification.setReferenceId(referenceID);
            notificationRepository.save(notification);
        });
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(UUID userId) {
        return notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getAll(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional(readOnly = true)
    public void markRead(Integer id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

}
