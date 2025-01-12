package com.example.picbooker.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.picbooker.user.User;

@Service
public class MessageServiceFactory {

    private final EmailService emailService;

    @Autowired
    public MessageServiceFactory(EmailService emailService) {
        this.emailService = emailService;

    }

    public MessageSenderService getMessageService(User user, User recipient) {
        if (!StringUtils.isBlank(recipient.getEmail()) && !StringUtils.isBlank(user.getEmail())) {
            return emailService;
        }
        // else if (!StringUtils.isBlank(recipient.getPhoneNumber()) &&
        // !StringUtils.isBlank(user.getPhoneNumber())) {
        // return null;
        // // sms service later if needed
        // }
        else {
            throw new IllegalArgumentException("Sender and Recipient must have either an email or a phone number");
        }
    }
}
