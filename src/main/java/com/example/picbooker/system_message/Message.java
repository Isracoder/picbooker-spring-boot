package com.example.picbooker.system_message;

import java.time.LocalDateTime;

import com.example.picbooker.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// to do refine entire message folder
// split -> chat messages , system messages 
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
