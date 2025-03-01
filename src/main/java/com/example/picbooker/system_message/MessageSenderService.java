package com.example.picbooker.system_message;

import com.example.picbooker.user.User;

public interface MessageSenderService {

    Message sendMessage(Message message, User sender);
}
