package com.example.picbooker.security.OauthToken;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.picbooker.security.AuthService;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OauthTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    // handle intercepting calls and updates access token to new one if expired ;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("Intercepting...");
        User user = UserService.getLoggedInUser();

        if (!isNull(user.getExpiresAt()) && user.getExpiresAt().isBefore(LocalDateTime.now())) {
            String newAccessToken = authService.refreshAccessToken(user.getRefreshToken(),
                    OauthProviderType.valueOf(request.getAttribute("Provider").toString()));
            request.setAttribute("Authorization", "Bearer " + newAccessToken);
        }

        return true;
    }
}
