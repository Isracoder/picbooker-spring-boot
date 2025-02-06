package com.example.picbooker.photographer;

import java.util.List;

import com.example.picbooker.user.UserResponse;
import com.example.picbooker.workhours.WorkHour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotographerResponse {
    private Long id;
    private UserResponse userResponse;
    private String studio;
    private String bio;

    private Integer bufferTimeMinutes; // buffer time between each sesion and the next

    private Integer minimumNoticeBeforeSessionMinutes;

    private List<WorkHour> workhours;

}
