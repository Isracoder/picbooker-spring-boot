package com.example.picbooker.system_message;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageResponse {
    private Long id;
    private Long senderId;
    private String text;
    private MessageStatus status;
    private LocalDateTime sentAt;
}
