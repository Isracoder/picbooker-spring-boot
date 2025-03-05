package com.example.picbooker.session;

import java.time.LocalDate;
import java.util.Currency;

import com.example.picbooker.photographer.PhotographerResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionSearchDTO {
    private PhotographerResponse photographerResponse;
    private LocalDate date;
    private Double price;
    private Currency currency;
    private Integer durationMinutes;
    private Double depositAmount;
    private String typeOrCustomType;
    // , date , session.getPricePerDuration() , session.getCurrency() ,
    // session.getDurationMinutes()
    // , session.getType() ? session.getType().toString() :
    // session.getCustomSessionType() ,
}
