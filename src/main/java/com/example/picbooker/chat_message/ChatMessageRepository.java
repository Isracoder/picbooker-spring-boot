package com.example.picbooker.chat_message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // this might need query, find by chat room id , sort by sent time desc ,limit
    // to num
    // List<ChatMessage> findLastMessagesByChatRoom(Long chatRoomId, Integer limit);
    Page<ChatMessage> findMessagesByChatRoom_IdOrderBySentAtDesc(Long chatRoomId,
            Pageable pageable);
}
