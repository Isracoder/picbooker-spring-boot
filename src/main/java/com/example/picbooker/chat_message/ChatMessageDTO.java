package com.example.picbooker.chat_message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

    private Long messageId;

    private Long chatRoomId;

    private Long senderId;

    private String content;

    private LocalDateTime sentAt;
}