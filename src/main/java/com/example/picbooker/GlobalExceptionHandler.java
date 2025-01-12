package com.example.picbooker;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.security.core.AuthenticationException;

import io.jsonwebtoken.ExpiredJwtException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ApiResponse<String> handleExpiredJwtException(ExpiredJwtException ex) {
        System.err.println("JWT expired: " + ex.getMessage());

        String message = "JWT token is expired";

        return ApiResponse.<String>builder()
                .content(message)
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<String> handleBadCredentialsException(BadCredentialsException ex) {
        System.err.println("Bad credentials: " + ex.getMessage());

        return ApiResponse.<String>builder()
                .content("Invalid credentials provided.")
                .status(HttpStatus.UNAUTHORIZED)
                .build();

    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<String> handleRunTimeException(RuntimeException ex) {
        System.err.println("RunTime Exception: " + ex.getMessage());
        HttpStatus stat = HttpStatus.BAD_REQUEST;
        String content = "Runtime Exception";
        if (ex.getMessage().contains("Expired") || ex.getMessage().contains("Access Denied")) {
            content = ex.getMessage();
            stat = HttpStatus.UNAUTHORIZED;
        }

        return ApiResponse.<String>builder()
                .content(content)
                .status(stat)
                .build();

    }

    @ExceptionHandler(ApiException.class)
    public ApiResponse<String> handleApiException(ApiException ex) {
        System.err.println("In handle APIException: " + ex.getMessage());

        return ApiResponse.<String>builder()
                .content(ex.getMessage())
                .status(ex.getStatus())
                .build();

    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGeneralException(Exception ex) {
        System.err.println("General Exception: " + ex.getMessage());
        String message = "General Exception";
        HttpStatus httpstatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof ExpiredJwtException) {
            message = "Expired token";
            httpstatus = HttpStatus.UNAUTHORIZED;
        }
        return ApiResponse.<String>builder()
                .content(message)
                .status(httpstatus)
                .build();

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<String> handleMissingTokenException(MissingServletRequestParameterException ex) {
        System.err.println("Missing token: " + ex.getMessage());

        return ApiResponse.<String>builder()
                .content("Token is missing.")
                .status(HttpStatus.BAD_REQUEST)
                .build();

    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<String> handleGeneralAuthException(AuthenticationException ex) {
        System.err.println("Auth exception: " + ex.getMessage());

        return ApiResponse.<String>builder()
                .content("Something went wrong while authenticating! Check credentials")
                .status(HttpStatus.UNAUTHORIZED)
                .build();

    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ApiResponse<String> handleGeneralInsufficientAuthenticationException(
            InsufficientAuthenticationException ex) {
        System.err.println("Auth exception: " + ex.getMessage());

        return ApiResponse.<String>builder()
                .content("Something went wrong while authenticating! Check credentials")
                .status(HttpStatus.UNAUTHORIZED)
                .build();

    }
}
