package com.example.picbooker.session.reschedule;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RescheduleDTO {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long sessionId;
}
