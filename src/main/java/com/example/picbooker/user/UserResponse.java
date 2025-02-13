package com.example.picbooker.user;

import java.time.LocalDateTime;
import java.util.Date;

import com.nimbusds.openid.connect.sdk.claims.Gender;

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
public class UserResponse {
    private Long id;

    private String username;

    private String email;

    private String phoneNumber;

    private Boolean isEmailVerified;

    private Long photographerId;

    private Long clientId;

    private Date DOB;

    private Gender gender;

    private String country;

    private String city;

    private LocalDateTime registerDate;

}
