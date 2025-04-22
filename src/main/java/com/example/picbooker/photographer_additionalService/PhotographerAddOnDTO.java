
package com.example.picbooker.photographer_additionalService;

import java.util.Date;

import com.example.picbooker.additionalService.AddOnType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotographerAddOnDTO {

    private AddOnType type;

    private String customSessionType;

    private Boolean multipleAllowedInSession;

    private Double fee;

    private Boolean isPrivate;

    private Boolean isSpecialOffer;

    private Date endDate;

    @Default
    private String currencyCode = "USD";

    private String description;

}
