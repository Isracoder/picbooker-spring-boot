package com.example.picbooker.session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.picbooker.deposit.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SessionDTO {

    private Long photographerId;
    private Long photographerSessionTypeId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String privateComment;
    private PaymentMethod paymentMethod;
    private List<Long> photographerAddOnIds; // 1 , 3
    // to do add modified client (name,phone,etc) info ;
    //

}
