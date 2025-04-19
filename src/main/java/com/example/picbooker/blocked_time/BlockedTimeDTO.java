package com.example.picbooker.blocked_time;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockedTimeDTO {
    private Long id;

    private Long photographerId;

    private String personalName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
