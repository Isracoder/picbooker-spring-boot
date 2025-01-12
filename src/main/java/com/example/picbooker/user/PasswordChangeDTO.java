package com.example.picbooker.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PasswordChangeDTO {

    private String email;

    private String oldPassword;

    private String newPassword;

}
