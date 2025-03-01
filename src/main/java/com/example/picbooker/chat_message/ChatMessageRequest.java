package com.example.picbooker.chat_message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {

    private String content;
    private Long recipientId;

}
