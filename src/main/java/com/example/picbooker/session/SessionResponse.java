package com.example.picbooker.session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.picbooker.client.ClientResponse;
import com.example.picbooker.deposit.DepositStatus;
import com.example.picbooker.deposit.PaymentMethod;
import com.example.picbooker.photographer.PhotographerResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionResponse {
    private Long sessionId;
    // private SessionDTO sessionDTO;
    private Long depositId;
    private PhotographerResponse photographerResponse;
    private SessionStatus sessionStatus;
    private DepositStatus depositStatus;
    private PaymentMethod paymentMethod;
    private Double totalPrice;
    private Double depositAmount;
    private String currency;
    private String typeOrCustomType;
    private String customClientName;
    private String customClientEmail;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String privateComment;
    private List<Long> photographerAddons;
    private ClientResponse clientResponse;
}
