package com.example.picbooker.photographer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCompletionDTO {
    // object with percentage : 70% , booleans representing (profile pic set ,
    // location , bio, workhours, social media, sessiontypes, portfolio)
    private Double completionPercentage;
    private Boolean profilePictureSet;
    private Boolean workHoursSet;
    private Boolean sessionTypesSet;
    private Boolean socialMediaSet;
    private Boolean portfolioSet;
    private Boolean locationSet;
    private Boolean emailVerified;
    private Boolean bioSet;
}
