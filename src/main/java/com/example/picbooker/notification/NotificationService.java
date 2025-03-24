package com.example.picbooker.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.picbooker.user.User;
import com.example.picbooker.web_socket.SocketNotification;
import com.example.picbooker.web_socket.SocketNotificationService;

import jakarta.transaction.Transactional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SocketNotificationService socketNotificationService; // For WebSockets

    @Transactional
    public void sendNotification(User user, String message) {
        Notification notification = Notification.builder().recipient(user).message(message).build();
        notificationRepository.save(notification);
        SocketNotification<String> socketNotification = new SocketNotification<String>(user.getId().toString(),
                message);
        socketNotificationService.notifyUserInSite(socketNotification);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipient_IdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
