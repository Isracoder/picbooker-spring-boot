package com.example.picbooker.chat_message;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    @Query("""
                SELECT cp.chatRoom.id FROM ChatParticipant cp
                WHERE cp.user.id IN (:user1, :user2)
                GROUP BY cp.chatRoom.id
                HAVING COUNT(DISTINCT cp.user.id) = 2
            """)
    Optional<Long> findChatRoomIdBetweenUsers(@Param("user1") Long user1, @Param("user2") Long user2);

    Boolean existsByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

    ChatParticipant findByUser_IdAndChatRoom_Id(Long userId, Long chatRoomId);
}
