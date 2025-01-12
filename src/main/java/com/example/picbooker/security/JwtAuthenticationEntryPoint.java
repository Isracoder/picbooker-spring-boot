package com.example.picbooker.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.picbooker.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        System.out.println("IN entry point");
        System.out.println(request.getPathInfo());
        System.out.println(request.getContextPath());
        System.out.println(authException);
        System.out.println(authException.getCause());
        System.out.println(authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String message = authException.getMessage();

        Throwable cause = authException.getCause();
        if (cause instanceof ExpiredJwtException) {
            message = "Token expired! Log in again.";
        } else if (authException instanceof InsufficientAuthenticationException) {
            message = "Authorization header is missing or incomplete!";
        } else if (authException instanceof BadCredentialsException) {
            message = "Invalid token provided!";
        }
        // Write the custom response as JSON
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper
                .writeValueAsString(new ApiResponse<String>(message, HttpStatus.UNAUTHORIZED)));

    }
}