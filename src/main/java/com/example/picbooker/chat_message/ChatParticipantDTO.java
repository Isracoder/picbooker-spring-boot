package com.example.picbooker.chat_message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatParticipantDTO {
    private Long chatParticipantId;
    private Long chatRoomId;
    private Long userId;
    private Integer unreadMessages;
    private String username;
    private String photoUrl;
    private String bio;
}
