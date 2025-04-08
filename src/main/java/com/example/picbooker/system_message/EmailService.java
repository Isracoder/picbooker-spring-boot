package com.example.picbooker.system_message;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.picbooker.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements MessageSenderService {

    private final JavaMailSender emailSender;
    private final RetryTemplate retryTemplate;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
        this.retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(300000); // 5 minutes between retries

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // Max 3 attempts

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);
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
        System.out.println("Email password : " + emailPassword);
        System.out.println("Email username : " + emailUsername);
        System.out.println("To: " + to);
        helper.setTo(to);

        helper.setSubject("Picbooker OTP Code");
        helper.setText("Your OTP code is: " + code);
        emailSender.send(message);
    }

    // public void sendGeneralEmail(String to, String subject, String textContent) {
    // try {
    // MimeMessage message = emailSender.createMimeMessage();
    // MimeMessageHelper helper = new MimeMessageHelper(message, true);
    // System.out.println(emailUsername + " " + emailPassword);
    // System.out.println(to + " " + subject + " " + textContent);
    // helper.setTo(to);
    // helper.setSubject(subject);
    // helper.setText(textContent);
    // emailSender.send(message);
    // System.out.println("After sending email");
    // } catch (MessagingException e) {
    // ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // scheduler.schedule(() -> {
    // sendGeneralEmail(to, subject, textContent);
    // }, 3, TimeUnit.MINUTES);
    // }
    // }

    @Async
    public void sendGeneralEmail(String to, String subject, String textContent) throws MessagingException {
        retryTemplate.execute(context -> {
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(textContent, true); // true = isHTML
                emailSender.send(message);
                return null;
            } catch (MessagingException e) {
                System.out.println("Email send failed (attempt {" + context.getRetryCount() + 1 + "}/3)");
                if (context.getRetryCount() >= 2) { // Final attempt failed
                    System.out.println("@@ EMAIL FAILED @@");
                    // failureHandler.handleFailedEmail(to, subject, textContent);
                }
                throw e; // Triggers retry
            }
        });
    }

}
