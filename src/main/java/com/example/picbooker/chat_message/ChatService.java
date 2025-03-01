package com.example.picbooker.chat_message;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;
import com.example.picbooker.web_socket.SocketNotification;
import com.example.picbooker.web_socket.SocketNotificationService;

@Service
public class ChatService {

    @Autowired
    private ChatParticipantRepository chatParticipantRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SocketNotificationService socketNotificationService;

    @Autowired
    private UserService userService;

    public ChatRoom createChatRoom() {
        return new ChatRoom();
    }

    public ChatRoom saveChatRoom(ChatRoom chat) {
        return chatRoomRepository.save(chat);
    }

    public ChatRoom createAndSaveChatRoom() {
        return saveChatRoom(createChatRoom());
    }

    public ChatMessage createChatMessage(ChatRoom chatRoom, User sender, String content, LocalDateTime sentAt) {
        return new ChatMessage(null, chatRoom, sender, content, sentAt);
    }

    public ChatMessage saveChatMessage(ChatMessage chat) {
        return chatMessageRepository.save(chat);
    }

    public ChatMessage createAndSaveChatMessage(ChatRoom chatRoom, User sender, String content, LocalDateTime sentAt) {
        return saveChatMessage(createChatMessage(chatRoom, sender, content, sentAt));
    }

    public ChatParticipant createChatParticipant(ChatRoom chatRoom, User user) {
        return new ChatParticipant(null, chatRoom, user);
    }

    public ChatParticipant saveChatParticipant(ChatParticipant chat) {
        return chatParticipantRepository.save(chat);
    }

    @Transactional
    public ChatParticipant createAndSaveChatParticipant(ChatRoom chatRoom, User user) {
        return saveChatParticipant(createChatParticipant(chatRoom, user));
    }

    @Transactional
    public ChatRoom getOrCreateChatRoom(Long user1Id, Long user2Id) {
        // Check if a chat room already exists
        Optional<Long> id = getChatRoomIdForPair(user1Id, user2Id);
        if (id.isPresent()) {
            Optional<ChatRoom> room = findById(id.get());
            if (room.isPresent())
                return room.get();
        }

        ChatRoom newChatRoom = createAndSaveChatRoom();
        User user1 = userService.findByIdThrow(user1Id);
        User user2 = userService.findByIdThrow(user2Id);
        createAndSaveChatParticipant(newChatRoom, user2);
        createAndSaveChatParticipant(newChatRoom, user1);
        return newChatRoom; // Return newly created chat room ID
    }

    @Transactional
    public Optional<Long> getChatRoomIdForPair(Long user1, Long user2) {
        Optional<Long> chatId = chatParticipantRepository.findChatRoomIdBetweenUsers(user1, user2);
        return chatId;
    }

    public Optional<ChatRoom> findById(Long id) {
        return chatRoomRepository.findById(id);
    }

    public ChatRoom findByIdThrow(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Chat not found by Id"));
    }

    @Transactional
    public void sendMessageToRoom(ChatMessageRequest messageRequest, User sender) {
        // how to send and mark as unread / vs read ?
        // first mark unread then if displayed call from web and mark read?
        ChatRoom chatRoom = getOrCreateChatRoom(sender.getId(), messageRequest.getRecipientId());
        ChatMessage message = toChatMessageEntity(messageRequest, sender, chatRoom);
        saveChatMessage(message);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", message.getSender().getId());
        data.put("message", message.getContent());
        socketNotificationService
                .notifyChat(new SocketNotification<Map<String, Object>>(chatRoom.getId().toString(), data));
    }

    public void getChatHistoryBetweenUsers(Long user1Id, Long user2Id) {
        // to do implement ;
        //
    }

    public void getUnreadMessages(Long user1Id, Long user2Id) {
        // to do implement
    }

    public void deleteMessage(Long messageId) {
        // to do implement
    }

    public void deleteEntireChat(Long chatRoomId) {
        // to do implement
    }

    public void getUsersWithActiveChatAndLastMessage(Long userId) {
        // to do implement ;
        // maybe return object {chatRoomId: , userId: , username: , lastMessageContent:
        // , lastMessageDate: , unRead: t/f , unReadCount: } ;
        // maybe this one just list of chatIds and other gets chat info
    }

    public void getChatRoomInfo(Long chatRoomId) {
        // maybe return object {userId: , username: , lastMessageContent:
        // , lastMessageDate: , unRead: t/f , unReadCount: } ;
        // maybe this one just list of chatIds and other gets chat info
    }

    public ChatMessage toChatMessageEntity(ChatMessageRequest request, User sender, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(request.getContent())
                .sender(sender)
                .build();
    }

}