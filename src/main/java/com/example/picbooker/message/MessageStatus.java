package com.example.picbooker.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageStatus {
    DELIVERED("Delivered"), PENDING("Pending"), FAILED("Failed");

    private final String status;

}
