package com.example.picbooker.security;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.picbooker.user.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-days}")
    private Integer jwtExpirationDays;

    // @Value("${app-jwt-expiration-milliseconds}")
    // private long jwtExpirationDate;

    public Boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parse(token.replace(" ", ""));
        return true;
    }

    public String generateJwtToken(Authentication authentication) {
        String email = authentication.getName(); // since i'm authenticating via email

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + TimeUnit.MILLISECONDS.convert(
                        jwtExpirationDays, TimeUnit.DAYS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateJwtToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + TimeUnit.MILLISECONDS.convert(
                        jwtExpirationDays, TimeUnit.DAYS)))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmail(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long getUserId(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }

    public String generateCode2FA() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }

}
