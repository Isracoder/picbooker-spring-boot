package com.example.picbooker.chat_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/history")
    public ApiResponse<String> findById() {
        // also have another for unread messages content and count
        // with user1Id , user2Id as params, or chatRoomId, limit, pagination
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();

    }

    @PutMapping("/history")
    public ApiResponse<String> markRead() {
        // send chat room id
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();

    }

    @GetMapping("/active")
    public ApiResponse<String> getUsersWithActiveChat() {
        // return users that I have a chat with
        // maybe last message as well to display
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();

    }

    // GET /chat/room?user1Id=2&user2Id=5
    @GetMapping("/room")
    public ApiResponse<String> getChatRoomBetweenUsers() {
        // return chat room info(id, last 10 messages, ...) for those 2 users
        //
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();

    }

    @PostMapping("/room")
    public ApiResponse<String> deleteChatRoom() {

        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();

    }

    @DeleteMapping("/message/{id}")
    public ApiResponse<String> deleteById(@PathVariable("id") long id) {

        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();

    }

    @PostMapping("/message")
    public ApiResponse<String> sendMessage() {
        // if no room exists create one
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();

        // {
        // "chatRoomId": "string",
        // "senderId": "string",
        // "message": "string"
        // }

    }

}