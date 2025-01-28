package com.example.picbooker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiException;
import com.example.picbooker.ApiResponse;
import com.example.picbooker.security.OauthToken.OauthProviderType;
import com.example.picbooker.security.passwordReset.PasswordResetDTO;
import com.example.picbooker.user.UserOTP;
import com.example.picbooker.user.UserRequest;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@RestController
@NoArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // for testing
    @GetMapping("/hi")
    public ApiResponse<String> sayHi() {
        try {

            return ApiResponse.<String>builder().content("HI")
                    .status(HttpStatus.OK).build();
        } catch (Exception e) {
            throw e;
        }
    }

    // to send code in case it expired, user took too long to get it
    @GetMapping("/code")
    public ApiResponse<String> resend2FAcode(@RequestBody String email) {
        try {
            authService.changeAndResendCode(email, null);
            return ApiResponse.<String>builder().content("Code has been sent to email")
                    .status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ApiResponse.<String>builder().content(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // 1st register step -> send otp
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserRequest user) {

        System.out.println("IN register");
        // UserResponse res = userService.save(user);
        authService.initiateRegister(user);
        return ApiResponse.<String>builder().status(HttpStatus.OK).content("2FA code sent to email successfully.")
                .build();

    }

    // 1st login step -> send otp (in case of 2fa enabled)
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody UserRequest userRequest) {
        try {
            System.out.println("IN login");
            String jwt = authService.initiateLogin(userRequest);
            return ApiResponse.<String>builder().status(HttpStatus.OK).content(jwt)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder().status(HttpStatus.BAD_REQUEST)
                    .content(e.getMessage())
                    .build();
        }
    }

    // 2nd register step -> get jwt
    @PostMapping("/verify-register")
    public ApiResponse<String> verify2FA(@RequestBody UserOTP verifyDto) {

        String token = authService.verifyRegister2FA(verifyDto);
        return ApiResponse.<String>builder().status(HttpStatus.OK).content(token)
                .build();

    }

    // @PostMapping("/verify-login")
    // public ApiResponse<String> verifyLogin2FA(@RequestBody UserOTP verifyDto) {

    // String token = authService.verifyLogin2FA(verifyDto);
    // return ApiResponse.<String>builder()
    // .status(HttpStatus.OK)
    // .content(token)
    // .build();

    // }

    // 1 email -> sends code
    @PostMapping("/forgot-password-request")
    public ApiResponse<String> forgotPassword(@RequestBody String email) {
        try {
            System.out.println("Email: " + email);
            authService.initiateChangePasswordRequest(email);
            return ApiResponse.<String>builder().status(HttpStatus.OK).content("Code sent to email successfully.")
                    .build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    // 2 enters code -> gets token
    @PostMapping("/verify-identity")
    public ApiResponse<String> verifyChangePasswordRequest(@RequestBody UserOTP userOTP) {
        try {
            return ApiResponse.<String>builder().status(HttpStatus.OK)
                    .content("Password Reset token:" + authService.verifyChangePasswordRequest(userOTP).getToken())
                    .build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    // 3 sends new password + (token from front only don't let user see)
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody PasswordResetDTO PasswordResetDTO) {
        try {
            authService.resetPassword(PasswordResetDTO);
            return ApiResponse.<String>builder().status(HttpStatus.OK).content("Password reset successfully.").build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    // forgot password reset flow : forgot password request (get 2fa to email),
    // verify identity (enter 2fa in app) , change password(enter password reset
    // code (front-end) , and new password)
    // change password flow: enter old and new passwords ,

    // oauth section

    // to do merge with other one and use query paramters ,
    // /api/auth/oauth2?provider=google maybe
    @GetMapping("/google")
    public ApiResponse<String> initiateGoogleLogin() {
        try {
            String authUrl = authService.getAuthUrl(OauthProviderType.GOOGLE);
            return ApiResponse.<String>builder().content(authUrl)
                    .status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ApiResponse.<String>builder().content(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // exchange code for access token apple and get jwt
    @GetMapping("/oauth2/code/google")
    public ApiResponse<String> getAuthCodeGoogle(@RequestParam("code") String code, @RequestParam("scope") String scope,
            @RequestParam("authuser") String authUser, @RequestParam("prompt") String prompt) {
        try {
            // login via google
            System.out.println("In get auth code");
            System.out.println("code: " + code);
            String jwt = authService.processGrantCode(code, OauthProviderType.GOOGLE);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK)
                    .content("Bearer token: " + jwt)
                    .build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }
}
