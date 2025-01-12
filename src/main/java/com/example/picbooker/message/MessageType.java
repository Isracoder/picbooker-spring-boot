package com.example.picbooker.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {
    SMS("SMS"), EMAIL("Email");

    private final String type;

}
