package com.example.picbooker.chat_message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {

    @NotNull(message = "content cannot be null")
    @NotBlank(message = "content must not be blank")
    private String content;

    @NotNull(message = "Recipient cannot be null")
    private Long recipientId;

}
