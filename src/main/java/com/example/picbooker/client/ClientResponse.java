package com.example.picbooker.client;

import com.example.picbooker.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private UserResponse userResponse;
    private int pointsBalance;
    private String personalName;

}
