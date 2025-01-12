package com.example.picbooker.message;

import org.springframework.validation.annotation.Validated;

import com.example.picbooker.user.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Validated
@Value
public class MessageRequest {

    @NotNull(message = "Sender ID cannot be null")
    @PositiveOrZero(message = "Sender ID must be a positive number")
    private Long SenderId;

    @NotNull(message = "Message text cannot be null")
    @Size(min = 1, message = "Message text must be at least 1 character")
    private String messageText;

    @NotNull(message = "Recipient shouldn't be null")
    private User recipient;

}
