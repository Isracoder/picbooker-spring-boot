package com.example.picbooker.client;

import com.example.picbooker.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client extends User {
    private int pointsBalance;

}
