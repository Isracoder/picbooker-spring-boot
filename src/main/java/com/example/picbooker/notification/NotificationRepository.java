package com.example.picbooker.notification;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient_IdAndReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findByRecipient_IdAndRead(Long userId, boolean read);
}
