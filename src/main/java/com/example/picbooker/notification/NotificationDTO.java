package com.example.picbooker.notification;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    @Default
    private boolean read = false; // Track if the notification has been seen

    private Long notificationId;

    private String content;

    private LocalDateTime sentAt;
}