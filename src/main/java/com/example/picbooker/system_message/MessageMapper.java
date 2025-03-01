package com.example.picbooker.system_message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MessageMapper {
    public static Message toEntity(MessageRequest messageDTO) {
        return Message.builder().senderId(messageDTO.getSenderId())
                .text(messageDTO.getMessageText())
                .recipient(messageDTO.getRecipient())
                .sentAt(LocalDateTime.now())
                .build();
    }

    public static MessageResponse toResponse(Message message) {
        return MessageResponse.builder().senderId(message.getSenderId()).text(message.getText())
                .status(message.getStatus())
                .sentAt(message.getSentAt()).build();
    }

    public static List<MessageResponse> toResponse(List<Message> messages) {
        return messages.stream().map(message -> toResponse(message))
                .collect(Collectors.toList());
    }
}
