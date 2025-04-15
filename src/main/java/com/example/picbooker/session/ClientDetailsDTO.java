package com.example.picbooker.session;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDetailsDTO {
    private String personalName;
    private String email;
    private String phoneNumber;
    private String city;
    private String country;
    private String gender;

}
