package com.example.picbooker.user;

import java.util.Date;

import com.nimbusds.openid.connect.sdk.claims.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private String username;

    private String email;

    private String password;
    private String photoURL;

    private Date DOB;

    private Gender gender;

    private String country;

    private String city;

    // private RoleType role;

}
