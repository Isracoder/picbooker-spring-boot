package com.example.picbooker.message;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.picbooker.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements MessageSenderService {

    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public Message sendMessage(Message message, User sender) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sender.getEmail());
        msg.setTo(message.getRecipient().getEmail());
        msg.setText(message.getText());
        emailSender.send(msg);

        message.setStatus(MessageStatus.DELIVERED);
        message.setType(MessageType.EMAIL);
        message.setSentAt(LocalDateTime.now());
        return message;
    }

    public void send2FACode(String to, String code) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Paranoia Zone 2FA Code");
        helper.setText("Your 2FA code is: " + code);
        emailSender.send(message);
    }
}
