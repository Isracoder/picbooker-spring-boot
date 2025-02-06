package com.example.picbooker.photographer;

import java.util.ArrayList;
import java.util.List;

import com.example.picbooker.workhours.WorkHour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotographerRequest {

    private String studio; // optional

    private String bio;

    @Default
    private Integer bufferTimeMinutes = 15; // buffer time between each sesion and the next

    @Default
    private Integer minimumNoticeBeforeSessionMinutes = 24 * 60; // set as one day ?
    // minimum notice time before booking a sesson in minutes ;

    // my workhours
    @Default
    List<WorkHour> workhours = new ArrayList<>();

}
