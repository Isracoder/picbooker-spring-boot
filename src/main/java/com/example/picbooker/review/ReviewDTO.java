package com.example.picbooker.review;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {

    private Long photographerId;
    private Long bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime commentTime; // maybe no need to send ;

}
