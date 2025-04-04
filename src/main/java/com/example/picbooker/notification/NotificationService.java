package com.example.picbooker.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.user.User;
import com.example.picbooker.web_socket.SocketNotification;
import com.example.picbooker.web_socket.SocketNotificationService;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SocketNotificationService socketNotificationService; // For WebSockets

    public List<NotificationDTO> getLastNotificationsForUser(Long userId, Pageable pageable) {

        List<NotificationDTO> notifications = notificationRepository
                .findByRecipient_IdOrderByCreatedAtDesc(userId, pageable)
                .stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toCollection(ArrayList::new));

        // Collections.reverse(notifications);
        return notifications;

    }

    @Transactional
    public void sendNotification(User user, String message) {
        Notification notification = Notification.builder().recipient(user).message(message).build();
        notificationRepository.save(notification);
        SocketNotification<Map<String, String>> socketNotification = new SocketNotification<Map<String, String>>(
                user.getId().toString(),
                Map.of("content", message, "id", notification.getId().toString(), "sentAt",
                        notification.getCreatedAt().toString()));
        socketNotificationService.notifyUserInSite(socketNotification);
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {

        List<NotificationDTO> notifications = notificationRepository
                .findByRecipient_IdAndReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::toNotificationResponse).toList();
        // Collections.reverse(notifications);
        // System.out.println(notifications);
        return notifications;
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {

        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {

        List<Notification> notifications = notificationRepository.findByRecipient_IdAndRead(userId, false);
        notifications.stream().forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public NotificationDTO toNotificationResponse(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getId())
                .content(notification.getMessage())
                .sentAt(notification.getCreatedAt())
                .read(notification.isRead())
                .build();
    }

}
