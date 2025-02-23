
package com.example.picbooker.photographer_additionalService;

import com.example.picbooker.additionalService.AddOnType;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder
public class PhotographerAddOnDTO {

    private AddOnType type;

    private String customSessionType;

    private Boolean multipleAllowedInSession;

    private Double fee;

    @Default
    private String currencyCode = "USD";

    private String description;

}
