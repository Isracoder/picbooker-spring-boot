package com.example.picbooker.photographer;

import java.util.ArrayList;
import java.util.List;

import com.example.picbooker.socialLinks.SocialLink;
import com.example.picbooker.user.UserResponse;
import com.example.picbooker.workhours.WorkHourDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
    private String personalName;
    private String photoUrl;
    @Default
    private Double rating = 0.0;
    @Default
    private Integer reviews = 0;

    private Integer bufferTimeMinutes; // buffer time between each session and the next

    private Integer minimumNoticeBeforeSessionMinutes;

    private List<WorkHourDTO> workhours;

    @Default
    List<SocialLink> socialLinks = new ArrayList<>();

}
