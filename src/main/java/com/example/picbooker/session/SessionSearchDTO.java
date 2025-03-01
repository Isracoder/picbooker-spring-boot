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
    // maybe time if possible
}
