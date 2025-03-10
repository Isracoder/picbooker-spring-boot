package com.example.picbooker.session;

import com.example.picbooker.deposit.DepositStatus;
import com.example.picbooker.deposit.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionResponse {
    private Long sessionId;
    private SessionDTO sessionDTO;
    private SessionStatus sessionStatus;
    private DepositStatus depositStatus;
    private Long depositId;
    private PaymentMethod paymentMethod;
    private Double totalPrice;
    private Double depositAmount;
}
