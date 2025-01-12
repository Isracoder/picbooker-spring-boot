package com.example.picbooker.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.user.UserService;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final UserService userService;

    private final MessageService messageService;

    @Autowired
    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("/")
    public ApiResponse<MessageResponse> sendMessage(@RequestBody MessageRequest messageDTO) {
        MessageResponse message = messageService.handleMessage(messageDTO, userService);
        return ApiResponse.<MessageResponse>builder()
                .content(message)
                .status(HttpStatus.OK)
                .build();

    }

}
