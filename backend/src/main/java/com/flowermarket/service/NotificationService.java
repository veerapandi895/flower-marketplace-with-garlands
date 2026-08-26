package com.flowermarket.service;

import com.flowermarket.entity.Notification;
import com.flowermarket.entity.User;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification notify(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Phase 13: lightweight feed for the navbar bell dropdown.
    public List<Notification> getRecentForUser(Long userId) {
        return notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void markRead(User user, Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Notification not found"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have access to this notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllRead(User user) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(user.getId());
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
