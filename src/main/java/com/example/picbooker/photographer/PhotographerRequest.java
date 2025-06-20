package com.example.picbooker.photographer;

import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Long userId;

    private String bio;

    private String photoURL;

    private String personalName;

    @Default
    private Integer bufferTimeMinutes = 15; // buffer time between each session and the next

    @Default
    private Integer minimumNoticeBeforeSessionMinutes = 1440 * 2; // set as two day ?
    // minimum notice time before booking a session in minutes ;

}
