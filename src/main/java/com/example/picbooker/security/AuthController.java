package com.example.picbooker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@RestController
@NoArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${app.web-redirect}")
    private String oauthredirectURL;

    @Value("${app.web-home-redirect}")
    private String homeRedirectURL;

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

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, @RequestBody UserRequest userRequest) {
        try {

            boolean isMobile = SecurityConfig.isMobile(request);
            System.out.println("IN login , ismobile: " + isMobile);
            String jwt = authService.initiateLogin(userRequest);

            if (isMobile) {
                return ApiResponse.<String>builder().status(HttpStatus.OK).content(jwt)
                        .build();
            } else {
                ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                        .httpOnly(true)
                        .secure(false) // Only in production with HTTPS
                        .path("/") // Available across the site
                        .maxAge(86400) // 1 day expiration
                        .build();

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());

                // Redirect user back to frontend (PostSignup if no role, Home otherwise)
                return ResponseEntity.ok()
                        .headers(headers)
                        .body("Login successful");

            }
        } catch (Exception e) {
            return ApiResponse.<String>builder().status(HttpStatus.BAD_REQUEST)
                    .content(e.getMessage())
                    .build();
        }
    }

    // 2nd register step -> get jwt
    @PostMapping("/verify-register")
    public ResponseEntity<String> verify2FA(HttpServletRequest request, @RequestBody UserOTP verifyDto) {

        boolean isMobile = SecurityConfig.isMobile(request);
        String jwt = authService.verifyRegister2FA(verifyDto);
        System.out.println("Is mobile: " + isMobile);
        if (isMobile) {
            return ApiResponse.<String>builder().status(HttpStatus.OK).content(jwt)
                    .build();
        } else {
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(false) // Only in production with HTTPS
                    .path("/") // Available across the site
                    .maxAge(86400) // 1 day expiration
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            // Redirect user back to frontend (PostSignup if no role, Home otherwise)
            return ResponseEntity.ok()
                    .headers(headers)
                    .body("Registration successful");
        }

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
    public ResponseEntity<Void> getAuthCodeGoogle(@RequestParam("code") String code,
            @RequestParam("scope") String scope,
            @RequestParam("authuser") String authUser, @RequestParam("prompt") String prompt,
            HttpServletResponse response) {

        try {
            System.out.println("In get auth code");
            System.out.println("code: " + code);
            String jwt = authService.processGrantCode(code, OauthProviderType.GOOGLE);

            // String redirectUrl = redirectURL + "?token=" + jwt; // Pass token in query
            // response.sendRedirect(redirectUrl);
            // Set HTTP-only cookie
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(false) // Only in production with HTTPS
                    .path("/") // Available across the site
                    .maxAge(86400) // 1 day expiration
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            // Redirect user back to frontend (PostSignup if no role, Home otherwise)
            return ResponseEntity.status(302)
                    .header("Location", oauthredirectURL)
                    .headers(headers)
                    .build();

        } catch (Exception e) {
            // TODO: handle exception
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Io Exception ");

        }
        // login via google

    }

}

// return ResponseEntity.status(HttpStatus.OK)
// .header(HttpHeaders.SET_COOKIE, "jwt=" + jwt + "; HttpOnly; Secure; ") // to
// do add same
// // site none
// // prod SameSite=None
// .header(HttpHeaders.LOCATION, redirectURL) // Redirect URL
// .build();
// return ResponseEntity.ok()
// .header(HttpHeaders.SET_COOKIE, "jwt=" + jwt + "; HttpOnly; Secure; ")
// .body("<script>window.location.href='" + redirectURL + "';</script>");

// ResponseEntity.ok()
// .header(HttpHeaders.SET_COOKIE, "jwt=" + jwt + "; HttpOnly; Secure;
// SameSite=None")
// .body(Map.of("redirectUrl", "http://localhost:3000/PostSignup"));

// Redirect without JWT in URL
// response.sendRedirect("http://localhost:3000/PostSignup");