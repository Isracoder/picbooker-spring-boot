package com.example.picbooker.system_message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {
    SMS("SMS"), EMAIL("Email");

    private final String type;

}
