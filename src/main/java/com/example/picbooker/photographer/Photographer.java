package com.example.picbooker.photographer;

import com.example.picbooker.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Photographer extends User {
    private String studio;

}
