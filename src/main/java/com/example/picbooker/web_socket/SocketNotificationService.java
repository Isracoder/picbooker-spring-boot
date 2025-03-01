package com.example.picbooker.web_socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public <T> void notifyChat(SocketNotification<T> notification) {
        try {
            System.out.println("IN notify chat");
            messagingTemplate.convertAndSend("/topic/room/" + notification.getRoomId(), notification.getData());

        } catch (Exception e) {
            System.out.println("ERROR in notify chat");
            System.out.println(e.getMessage());
        }
        // Notify all players in the room
    }

}
