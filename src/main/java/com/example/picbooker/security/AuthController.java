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
import com.example.picbooker.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@RestController
@NoArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody UserRequest user) {
        try {
            System.out.println("IN register");
            // UserResponse res = userService.save(user);
            authService.initiateRegister(user);
            return ApiResponse.<String>builder().status(HttpStatus.OK).content("2FA code sent to email successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder().status(HttpStatus.BAD_REQUEST).content("Something went wrong creating a user")
                    .build();
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody UserRequest userRequest) {
        try {
            System.out.println("IN login");
            authService.initiateLogin(userRequest);
            return ApiResponse.builder().status(HttpStatus.OK).content("2FA sent to email successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder().status(HttpStatus.BAD_REQUEST)
                    .content(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/verify-register")
    public ApiResponse<?> verify2FA(@RequestBody UserOTP verifyDto) {
        try {
            UserResponse userRes = authService.verifyRegister2FA(verifyDto);

            return new ApiResponse<UserResponse>(userRes, HttpStatus.OK);
        } catch (ApiException e) {
            return ApiResponse.builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/verify-login")
    public ApiResponse<?> verifyLogin2FA(@RequestBody UserOTP verifyDto) {
        try {
            String token = authService.verifyLogin2FA(verifyDto);
            return ApiResponse.builder().status(HttpStatus.OK).content(token)
                    .build();
        } catch (ApiException e) {
            return ApiResponse.builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    // 1 email -> sends code
    @PostMapping("/forgot-password-request")
    public ApiResponse<?> forgotPassword(@RequestBody String email) {
        try {
            System.out.println("Email: " + email);
            authService.initiateChangePasswordRequest(email);
            return ApiResponse.builder().status(HttpStatus.OK).content("Code sent to email successfully.").build();
        } catch (ApiException e) {
            return ApiResponse.builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    // 2 enters code -> gets token
    @PostMapping("/verify-identity")
    public ApiResponse<?> verifyChangePasswordRequest(@RequestBody UserOTP userOTP) {
        try {
            return ApiResponse.builder().status(HttpStatus.OK)
                    .content("Password Reset token:" + authService.verifyChangePasswordRequest(userOTP).getToken())
                    .build();
        } catch (ApiException e) {
            return ApiResponse.builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    // 3 sends new password + (token from front only don't let user see)
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestBody PasswordResetDTO PasswordResetDTO) {
        try {
            authService.resetPassword(PasswordResetDTO);
            return ApiResponse.builder().status(HttpStatus.OK).content("Password reset successfully.").build();
        } catch (ApiException e) {
            return ApiResponse.builder()
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
    public ApiResponse<Object> initiateGoogleLogin() {
        try {
            String authUrl = authService.getAuthUrl(OauthProviderType.GOOGLE);
            return ApiResponse.builder().content(authUrl)
                    .status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ApiResponse.builder().content(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // exchange code for access token apple and get jwt
    @GetMapping("/oauth2/code/google")
    public ApiResponse<?> getAuthCodeGoogle(@RequestParam("code") String code, @RequestParam("scope") String scope,
            @RequestParam("authuser") String authUser, @RequestParam("prompt") String prompt) {
        try {
            // login via google
            System.out.println("In get auth code");
            System.out.println("code: " + code);
            String jwt = authService.processGrantCode(code, OauthProviderType.GOOGLE);
            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .content("Bearer token: " + jwt)
                    .build();
        } catch (ApiException e) {
            return ApiResponse.builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    // initiate apple login request
    @GetMapping("/apple")
    public ApiResponse<Object> initiateAppleLogin() {
        try {
            String authUrl = authService.getAuthUrl(OauthProviderType.APPLE);
            return ApiResponse.builder().content(authUrl)
                    .status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ApiResponse.builder().content(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // exchange code for access token apple and get jwt
    // example of uri :
    // https://your-app.com/auth/oauth2/code/apple?code=auth_code&state=random_string&id_token=jwt_token
    // https://appleid.apple.com/auth/authorize?client_id=com.cotede.paranoia.service&redirect_uri=https://honest-beagle-legible.ngrok-free.app/api/auth/oauth2/code/apple&response_type=code&scope=name,email&state=c7b4240e-0202-4260-a2ae-37390b76f0f4
    // http://localhost:8080/api/auth/oauth2/code/apple

    // /api/auth/oauth2/code/apple
    @PostMapping("/oauth2/code/apple")
    public ApiResponse<?> getAuthCodeApple(@RequestParam(value = "code") String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "id_token", required = false) String idToken,
            @RequestParam(value = "user", required = false) String user) {
        try {
            // login via apple
            System.out.println(user);
            System.out.println("id token: " + idToken);
            System.out.println("In get auth code apple");
            System.out.println("code: " + code);
            // pass in id token as well
            String jwt = authService.processGrantCode(code, OauthProviderType.APPLE);
            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .content("Bearer token: " + jwt)
                    .build();
        } catch (ApiException e) {
            return ApiResponse.builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

}
