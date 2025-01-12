package com.example.picbooker.message;

import java.time.LocalDateTime;

import com.example.picbooker.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Long senderId;

    private String text;

    private User recipient;

    private MessageType type;

    private MessageStatus status;

    private LocalDateTime sentAt;
}
