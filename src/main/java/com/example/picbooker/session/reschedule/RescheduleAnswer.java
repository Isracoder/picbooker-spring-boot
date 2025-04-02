package com.example.picbooker.session.reschedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RescheduleAnswer {
    private Long sessionId;
    private RescheduleStatus status;
}
