package com.example.picbooker.security.passwordReset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PasswordResetDTO {

    private String resetToken;

    private String newPassword;

}
