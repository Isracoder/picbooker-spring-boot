package com.example.picbooker.message;

import com.example.picbooker.user.User;

public interface MessageSenderService {

    Message sendMessage(Message message, User sender);
}
