package com.example.picbooker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "https://localhost:8080", // Local API testing (HTTPS)
                                "http://localhost:8080", // Local API testing (HTTP)
                                "https://127.0.0.1:8000", // Local testing
                                "https://127.0.0.1:3000",
                                "http://127.0.0.1:3000",
                                "http://localhost:3000",
                                "https://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
                // "http://localhost:3000/PostSignup",
            }
        };
    }
}
