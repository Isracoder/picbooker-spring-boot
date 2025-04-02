package com.example.picbooker.web_socket;

import java.nio.charset.StandardCharsets;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

// to think what is this for 
@Component
public class StompInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        System.out.println("Channel Interceptor");

        MessageHeaders headers = message.getHeaders();
        System.out.println(headers);
        System.out.println(message);
        byte[] payload = (byte[]) message.getPayload();
        String body = new String(payload, StandardCharsets.UTF_8);
        System.out.println("Received message body: " + body);
        return message;
    }
}
