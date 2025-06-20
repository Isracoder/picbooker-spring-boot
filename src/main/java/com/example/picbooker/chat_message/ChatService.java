package com.example.picbooker.chat_message;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
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

    public ChatRoomDTO getChatRoomByIdThrow(Long chatRoomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Room Not found"));
        return toChatRoomResponse(room, userId, 5);
    }

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

    public ChatParticipant findChatParticipantByChatAndUserThrow(Long chatId, Long userId) {
        ChatParticipant chatParticipant = chatParticipantRepository.findByUser_IdAndChatRoom_Id(userId, chatId);
        if (isNull(chatParticipant))
            throw new ApiException(HttpStatus.NOT_FOUND, "Chat participant not found");
        return chatParticipant;
    }

    public ChatMessage findChatMessageByIdThrow(Long id) {
        return chatMessageRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Message not found by Id"));
    }

    @Transactional
    public void markChatAsRead(Long userId, Long chatRoomId) {
        ChatParticipant participant = chatParticipantRepository.findByUser_IdAndChatRoom_Id(userId, chatRoomId);
        if (isNull(participant)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User or Room not found.");
        }
        participant.setUnreadMessageCount(0);
        chatParticipantRepository.save(participant);

    }

    @Transactional
    void increaseUnreadMessageCount(Long userId, Long chatRoomId) {
        ChatParticipant participant = chatParticipantRepository.findByUser_IdAndChatRoom_Id(userId, chatRoomId);
        if (isNull(participant)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User or Room not found.");
        }
        participant.setUnreadMessageCount(
                (isNull(participant.getUnreadMessageCount()) ? 0 : participant.getUnreadMessageCount()) + 1);
        chatParticipantRepository.save(participant);
    }

    @Transactional
    public ChatMessageDTO sendMessageToRoom(ChatMessageRequest messageRequest, User sender) {
        // how to send and mark as unread / vs read ?
        // first mark unread then if displayed call from web and mark read?
        ChatRoom chatRoom = getOrCreateChatRoom(sender.getId(), messageRequest.getRecipientId());
        ChatMessage message = toChatMessageEntity(messageRequest, sender, chatRoom);
        message = saveChatMessage(message);
        increaseUnreadMessageCount(messageRequest.getRecipientId(), chatRoom.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", message.getSender().getId());
        data.put("message", message.getContent());
        data.put("chatRoomId", chatRoom.getId());
        socketNotificationService
                .notifyChat(new SocketNotification<Map<String, Object>>(chatRoom.getId().toString(), data));
        return toChatMessageResponse(message);
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
        // to do add deletion event web socket
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
                .map(chatRoom -> toChatRoomResponse(chatRoom, userId, 1))
                .toList();
    }

    public List<ChatMessageDTO> getUnreadMessagesForRoomAndUser(Long chatRoomId, Long userId) {
        ChatParticipant chatParticipant = findChatParticipantByChatAndUserThrow(chatRoomId, userId);
        System.out.println("Unread messages: " + chatParticipant.getUnreadMessageCount());
        if (chatParticipant.getUnreadMessageCount() == null || chatParticipant.getUnreadMessageCount() == 0)
            return new ArrayList<>();
        return getLastMessages(chatRoomId, Pageable.ofSize(chatParticipant.getUnreadMessageCount()));
    }

    public List<ChatMessageDTO> getLastMessages(Long chatRoomId, Pageable pageable) {
        List<ChatMessageDTO> messages = chatMessageRepository
                .findMessagesByChatRoom_IdOrderBySentAtDesc(chatRoomId, pageable)
                .stream()
                .map(this::toChatMessageResponse)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(messages);
        return messages;
    }

    public List<ChatMessageDTO> getLastMessagesForMyRoom(Long chatRoomId, Long userId, Pageable pageable) {
        if (!inChatRoom(userId, chatRoomId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your chat room.");
        }
        return getLastMessages(chatRoomId, pageable);
    }

    public ChatRoomDTO getChatRoomInfo(Long chatRoomId) {
        ChatRoom chatRoom = findChatRoomByIdThrow(chatRoomId);
        return ChatRoomDTO.builder()
                .chatRoomId(chatRoomId)
                .participants(chatRoom.getParticipants().stream().map(this::toChatParticipantDTO).toList())
                .lastMessages(getLastMessages(chatRoomId, Pageable.ofSize(defaultLastMessages)))
                .build();

    }

    public ChatParticipantDTO toChatParticipantDTO(ChatParticipant participant) {
        Photographer photographer = participant.getUser().getPhotographer();
        String bio = photographer != null ? photographer.getBio() : null;
        String photoUrl = photographer != null ? photographer.getProfilePhotoUrl() : null;
        return new ChatParticipantDTO(participant.getId(), participant.getChatRoom().getId(),
                participant.getUser().getId(), participant.getUnreadMessageCount(), participant.getUser().getUsername(),
                photoUrl, bio);
    }

    public ChatRoomDTO getChatRoomInfoForPair(Long user1Id, Long user2Id) {
        ChatRoom chatRoom = findChatRoomByIdThrow(getChatRoomIdForPair(user1Id, user2Id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No Chat room found for users")));
        // return ChatRoomDTO.builder()
        // .chatRoomId(chatRoom.getId())
        // .userIds(chatRoom.getParticipants().stream().map(participant ->
        // participant.getUser().getId())
        // .collect(Collectors.toList()))
        // .lastMessages(getLastMessages(chatRoom.getId(),
        // Pageable.ofSize(defaultLastMessages)))
        // // .unreadMessageCount(defaultLastMessages)
        // .build();
        return toChatRoomResponse(chatRoom, user1Id, defaultLastMessages);

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

    public ChatRoomDTO toChatRoomResponse(ChatRoom chatRoom, Long userId, Integer lastMessages) {

        Comparator<ChatParticipant> senderFirstComparator = Comparator
                .comparing(p -> !p.getUser().getId().equals(userId));
        return ChatRoomDTO.builder()
                .chatRoomId(chatRoom.getId())
                .participants(chatRoom.getParticipants().stream()
                        .sorted(senderFirstComparator)
                        .map(this::toChatParticipantDTO)
                        .toList())

                .unreadMessageCount(chatRoom.getParticipants().stream()
                        .filter(chatParticipant -> chatParticipant.getUser().getId() == userId).findAny()
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found in chat room"))
                        .getUnreadMessageCount()) // gets unread messages for specific user
                .lastMessages(
                        getLastMessages(chatRoom.getId(), Pageable.ofSize(isNull(lastMessages) ? 1 : lastMessages))) // think
                                                                                                                     // of
                                                                                                                     // keeping
                                                                                                                     // 1
                                                                                                                     // or
                                                                                                                     // 10
                                                                                                                     // ?
                .build();
    }

}