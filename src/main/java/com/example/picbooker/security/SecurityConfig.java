package com.example.picbooker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter authenticationFilter,
            JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static Boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone")
                || userAgent.contains("ReactNative"));

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> {
                    authorize.requestMatchers("/api/auth/**", "/css/**", "/v3/api-docs/**",
                            "/api/payments/**",

                            "/swagger-ui/**",
                            "/swagger-ui.html", "/js/**",
                            "/oauth2/**",

                            "/images/**",
                            "/webjars/**",
                            "/**.css", "/**.js",
                            "/**.png", "/**.jpg", "/ws/**", "/wss/**", "/", "/**.html").permitAll();

                    authorize.requestMatchers(HttpMethod.GET, "/api/media/gallery", "/api/users/*",
                            "/api/media/profile", "/api/*/reviews", "/api/reviews/*", "/api/sessions/appointments/*",
                            "/api/sessions/possibles",
                            "/api/photographers/*/session-types",

                            "/api/photographers/*/add-ons",
                            "/api/photographers/*/blocks",
                            "/api/photographers/*/work-hours",
                            "/api/photographers/*/socials",
                            "/api/photographers/*/profile-completion",
                            "/api/photographers/*/reviews",
                            "/api/photographers/*/info"

                ).permitAll();

                    authorize.requestMatchers(HttpMethod.OPTIONS);
                    authorize.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults());

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        System.out.println("Security config initialized");
        return http.build();
    }
}
