package com.example.picbooker.chat_message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // List<ChatRoom> findByUserIdSortedByLastMessage(Long userId);
    @Query("""
                SELECT cr FROM ChatRoom cr
                JOIN cr.participants p
                LEFT JOIN cr.messages m
                WHERE p.user.id = :userId
                GROUP BY cr.id
                ORDER BY MAX(m.sentAt) DESC
            """)
    List<ChatRoom> findByUserIdSortedByLastMessage(@Param("userId") Long userId);

}
