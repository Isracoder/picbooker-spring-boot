package com.example.picbooker.chat_message;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private Integer defaultLastMessages = 10;

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
        return new ChatParticipant(null, chatRoom, user, 0);
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
            Optional<ChatRoom> room = findChatRoomById(id.get());
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

    public Optional<ChatRoom> findChatRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    public ChatRoom findChatRoomByIdThrow(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Chat not found by Id"));
    }

    public Optional<ChatMessage> findChatMessageById(Long id) {
        return chatMessageRepository.findById(id);
    }

    public ChatMessage findChatMessageByIdThrow(Long id) {
        return chatMessageRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Message not found by Id"));
    }

    @Transactional
    public void markChatAsRead(Long userId, Long chatRoomId) {
        ChatParticipant participant = chatParticipantRepository.findByUserAndChat(userId, chatRoomId);
        if (isNull(participant)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User or Room not found.");
        }
        participant.setUnreadMessageCount(0);
        chatParticipantRepository.save(participant);

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

    @Transactional
    public void deleteMyMessage(Long senderId, Long messageId) {
        if (!isMyMessage(senderId, messageId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your message");
        }
        chatMessageRepository.deleteById(messageId);

    }

    @Transactional
    public void deleteMyChat(Long senderId, Long chatRoomId) {
        if (!inChatRoom(senderId, chatRoomId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your room");
        }
        deleteEntireChat(chatRoomId);

    }

    public Boolean isMyMessage(Long senderId, Long messageId) {
        ChatMessage message = findChatMessageByIdThrow(messageId);
        return (message.getSender().getId() == senderId);
    }

    public Boolean inChatRoom(Long user, Long chatRoomId) {
        return chatParticipantRepository.existsByChatRoom_IdAndUser_Id(chatRoomId, user);

    }

    @Transactional
    public void deleteEntireChat(Long chatRoomId) {
        // to think , should this delete from one side or both ?
        chatRoomRepository.deleteById(chatRoomId);
    }

    // for sidebar
    public List<ChatRoomDTO> getUserChats(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserIdSortedByLastMessage(userId);

        return chatRooms.stream()
                .map(chatRoom -> toChatRoomResponse(chatRoom, userId))
                .toList();
    }

    public List<ChatMessageDTO> getLastMessages(Long chatRoomId, Integer limit) {
        return chatMessageRepository.findLastMessagesByChatRoom(chatRoomId, limit).stream()
                .map(this::toChatMessageResponse).toList();
    }

    public List<ChatMessageDTO> getLastMessagesForMyRoom(Long chatRoomId, Long userId, Integer limit) {
        if (!inChatRoom(userId, chatRoomId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your chat room.");
        }
        return getLastMessages(chatRoomId, limit);
    }

    public ChatRoomDTO getChatRoomInfo(Long chatRoomId) {
        ChatRoom chatRoom = findChatRoomByIdThrow(chatRoomId);
        return ChatRoomDTO.builder()
                .chatRoomId(chatRoomId)
                .userIds(chatRoom.getParticipants().stream().map(participant -> participant.getId())
                        .collect(Collectors.toList()))
                .lastMessages(getLastMessages(chatRoomId, defaultLastMessages))
                .build();

    }

    public ChatRoomDTO getChatRoomInfoForPair(Long user1Id, Long user2Id) {
        ChatRoom chatRoom = findChatRoomByIdThrow(getChatRoomIdForPair(user1Id, user2Id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No Chat room found for users")));
        return ChatRoomDTO.builder()
                .chatRoomId(chatRoom.getId())
                .userIds(chatRoom.getParticipants().stream().map(participant -> participant.getId())
                        .collect(Collectors.toList()))
                .lastMessages(getLastMessages(chatRoom.getId(), defaultLastMessages))
                .build();

    }

    public ChatMessage toChatMessageEntity(ChatMessageRequest request, User sender, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(request.getContent())
                .sender(sender)
                .build();
    }

    public ChatMessageDTO toChatMessageResponse(ChatMessage message) {
        return ChatMessageDTO.builder()
                .chatRoomId(message.getChatRoom().getId())
                .messageId(message.getId())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .senderId(message.getSender().getId())
                .build();
    }

    public ChatRoomDTO toChatRoomResponse(ChatRoom chatRoom, Long userId) {
        return ChatRoomDTO.builder()
                .chatRoomId(chatRoom.getId())
                .userIds(chatRoom.getParticipants().stream()
                        .map(participant -> participant.getUser().getId()).toList())
                .unreadMessageCount(chatRoom.getParticipants().stream().findAny()
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found in chat room"))
                        .getUnreadMessageCount()) // gets unread messages for specific user
                .lastMessages(getLastMessages(chatRoom.getId(), 1)) // think of keeping 1 or 10 ?
                .build();
    }

}