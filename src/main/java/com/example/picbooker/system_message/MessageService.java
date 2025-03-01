package com.example.picbooker.system_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

@Service
@Transactional
public class MessageService {

    private final MessageServiceFactory messageServiceFactory;

    @Autowired
    public MessageService(MessageServiceFactory messageServiceFactory) {
        this.messageServiceFactory = messageServiceFactory;

    }

    public MessageResponse handleMessage(MessageRequest messageDTO, UserService userService) {

        User user = userService.findByIdThrow(messageDTO.getSenderId());

        MessageSenderService service = messageServiceFactory.getMessageService(user,
                messageDTO.getRecipient());

        Message message = service.sendMessage(MessageMapper.toEntity(messageDTO),
                user);
        return MessageMapper.toResponse(message);
    }
}
