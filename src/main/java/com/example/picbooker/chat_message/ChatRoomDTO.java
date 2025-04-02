package com.example.picbooker.chat_message;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDTO {
    private Long chatRoomId;
    private List<ChatParticipantDTO> participants;
    private List<ChatMessageDTO> lastMessages;
    private Integer unreadMessageCount;
    // private List<ChatParticipantDTO> chatParticipants;

}
