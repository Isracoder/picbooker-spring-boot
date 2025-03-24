package com.example.picbooker.session.reschedule;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RescheduleRequestRepository extends JpaRepository<RescheduleRequest, Long> {
    Optional<RescheduleRequest> findBySessionIdAndStatus(Long sessionId, RescheduleStatus status);

    Boolean existsBySession_IdAndStatus(Long sessionId, RescheduleStatus status);
}
