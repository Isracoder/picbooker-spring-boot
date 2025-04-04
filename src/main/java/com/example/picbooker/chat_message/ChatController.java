package com.example.picbooker.chat_message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

        @Autowired
        private ChatService chatService;

        // to do paginate
        @GetMapping("/{chatRoomId}/messages")
        public ApiResponse<List<ChatMessageDTO>> getMessageHistory(@PathVariable("chatRoomId") Long chatRoomId,
                        @PageableDefault Pageable pageable) {
                List<ChatMessageDTO> messages = chatService.getLastMessagesForMyRoom(chatRoomId,
                                UserService.getLoggedInUserThrow().getId(), pageable);
                return ApiResponse.<List<ChatMessageDTO>>builder()
                                .content(messages)
                                .status(HttpStatus.OK)
                                .build();

        }

        @PutMapping("/{chatRoomId}/read")
        public ApiResponse<String> markRead(@PathVariable("chatRoomId") Long chatRoomId) {
                // send chat room id
                chatService.markChatAsRead(UserService.getLoggedInUserThrow().getId(), chatRoomId);
                return ApiResponse.<String>builder()
                                .content("Success")
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/{chatRoomId}/unread")
        public ApiResponse<List<ChatMessageDTO>> getUnreadMessages(@PathVariable("chatRoomId") Long chatRoomId) {
                List<ChatMessageDTO> messages = chatService.getUnreadMessagesForRoomAndUser(chatRoomId,
                                UserService.getLoggedInUserThrow().getId());
                return ApiResponse.<List<ChatMessageDTO>>builder()
                                .content(messages)
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/active")
        public ApiResponse<List<ChatRoomDTO>> getUsersWithActiveChat() {
                // return users that I have a chat with
                // maybe last message as well to display
                return ApiResponse.<List<ChatRoomDTO>>builder()
                                .content(chatService.getUserChats(UserService.getLoggedInUserThrow().getId()))
                                .status(HttpStatus.OK)
                                .build();

        }

        // GET /chat/room?user1Id=2&user2Id=5
        @GetMapping("/room")
        public ApiResponse<ChatRoomDTO> getChatRoomBetweenUsers(@RequestParam("user1") Long user1Id,
                        @RequestParam("user2") Long user2Id) {
                // return chat room info(id, last 10 messages, ...) for those 2 users
                // to do check that i'm one of them via login, send that as user1 to function
                return ApiResponse.<ChatRoomDTO>builder()
                                .content(chatService.getChatRoomInfoForPair(user1Id, user2Id))
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/room/{chatRoomId}")
        public ApiResponse<ChatRoomDTO> getChatRoomById(@PathVariable("chatRoomId") Long chatRoomId) {
                // return chat room info(id, last 10 messages, ...) for those 2 users
                // to do check that i'm one of them via login, send that as user1 to function
                return ApiResponse.<ChatRoomDTO>builder()
                                .content(chatService.getChatRoomByIdThrow(chatRoomId,
                                                UserService.getLoggedInUserThrow().getId()))
                                .status(HttpStatus.OK)
                                .build();

        }

        // @DeleteMapping("/room/{chatRoomId}")
        // public ApiResponse<String> deleteChatRoom(@PathVariable("chatRoomId") long
        // chatRoomId) {
        // chatService.deleteMyChat(chatRoomId,
        // UserService.getLoggedInUserThrow().getId());
        // return ApiResponse.<String>builder()
        // .content("Success")
        // .status(HttpStatus.OK)
        // .build();

        // }

        @DeleteMapping("/message/{messageId}")
        public ApiResponse<String> deleteById(@PathVariable("messageId") long messageId) {

                chatService.deleteMyMessage(UserService.getLoggedInUserThrow().getId(), messageId);
                return ApiResponse.<String>builder()
                                .content("Success")
                                .status(HttpStatus.OK)
                                .build();

        }

        // to do change to return created message entity
        @PostMapping("/message")
        public ApiResponse<ChatMessageDTO> sendMessage(@RequestBody @Valid ChatMessageRequest messageRequest) {
                // if no room exists create one
                ChatMessageDTO chatMessageDTO = chatService.sendMessageToRoom(messageRequest,
                                UserService.getLoggedInUserThrow());
                return ApiResponse.<ChatMessageDTO>builder()
                                .content(chatMessageDTO)
                                .status(HttpStatus.OK)
                                .build();

                // {
                // "chatRoomId": "string",
                // "senderId": "string",
                // "message": "string"
                // }

        }

}