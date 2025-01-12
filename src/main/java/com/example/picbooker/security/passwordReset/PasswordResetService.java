package com.example.picbooker.security.passwordReset;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.picbooker.user.User;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetToken assignResetToken(User user) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(generateToken());
        resetToken.setExpiry(Instant.now().plus(10, ChronoUnit.MINUTES));
        resetToken.setUser(user);
        passwordResetTokenRepository.save(resetToken);
        return resetToken;

    }

    public void invalidateResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("invalid reset token"));
        resetToken.setExpiry(Instant.now());
        passwordResetTokenRepository.save(resetToken);
    }

    private String generateToken() {
        String token = UUID.randomUUID().toString();
        return token;

    }

    public User validateResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("invalid reset token"));
        if (resetToken.getExpiry().isAfter(Instant.now())) {
            return resetToken.getUser();
        }
        return null;
    }
}
