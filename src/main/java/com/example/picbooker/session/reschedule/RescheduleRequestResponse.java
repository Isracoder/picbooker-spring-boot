package com.example.picbooker.session.reschedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.picbooker.session.SessionResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleRequestResponse {
    private Long id;

    private Long initiatedById;

    private String reason;

    private SessionResponse sessionResponse;

    private LocalDate newDate;

    private LocalTime newStartTime;

    private LocalTime newEndTime;

    private LocalDateTime requestTimestamp;

    private RescheduleStatus status;

}
