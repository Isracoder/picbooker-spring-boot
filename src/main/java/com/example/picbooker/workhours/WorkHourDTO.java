package com.example.picbooker.workhours;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkHourDTO {

    private LocalTime startTime;

    private LocalTime endTime;

    @NotNull
    private DayOfWeek day;
}
