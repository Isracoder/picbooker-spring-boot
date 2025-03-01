package com.example.picbooker.web_socket;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocketNotificationController {

    @MessageMapping("/notifyRoom/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public SocketNotification<Object> notifyPlayers(@DestinationVariable String roomId,
            SocketNotification<Object> notification) {
        System.out.println("IN socket notification notify players , room id : " + roomId.toString());
        notification.setRoomId(roomId);
        System.out.println(notification.getData());
        System.out.println("Sending to destination: " + "/topic/room/" + roomId);

        return notification;
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public SocketNotification<String> receiveMessage(@DestinationVariable String roomId, Map<String, Object> message) {
        // System.out.println("In receive message");
        String msg = message.get("message").toString();

        // Map<String, String> response = new HashMap<>();
        String returnString = message.get("userId") + ": " + msg;

        return new SocketNotification<String>(roomId, returnString);
    }

}
