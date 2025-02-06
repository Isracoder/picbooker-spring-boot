package com.example.picbooker.workhours;

import java.time.DayOfWeek;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkHourDTO {

    private Integer startHour;

    private Integer endHour;

    private DayOfWeek day;
}
