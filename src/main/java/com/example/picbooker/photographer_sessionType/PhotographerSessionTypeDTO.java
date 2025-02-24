package com.example.picbooker.photographer_sessionType;

import com.example.picbooker.sessionType.SessionTypeName;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder
public class PhotographerSessionTypeDTO {

    private SessionTypeName type;

    private String customSessionType;

    private Double pricePerDuration;

    @Default
    private Integer durationMinutes = 60;

    private String description;

    private String location;

    private Boolean isPrivate;

    private Boolean requiresDeposit;

    private Double depositAmount;

    @Default
    private String currencyCode = "USD";

}
