package com.example.picbooker.session.reschedule;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RescheduleRequestRepository extends JpaRepository<RescheduleRequest, Long> {
    Optional<RescheduleRequest> findBySessionIdAndStatus(Long sessionId, RescheduleStatus status);

    @Query("SELECT r FROM RescheduleRequest r WHERE r.session.photographer.id = :photographerId AND r.status = :status AND r.initiatedById != :userId ")
    Page<RescheduleRequest> findForPhotographerAndStatus(@Param("photographerId") Long photographerId,
            @Param("userId") Long userId, @Param("status") RescheduleStatus status, Pageable pageable);

    @Query("SELECT r FROM RescheduleRequest r WHERE r.session.client.id = :clientId AND r.status = :status AND r.initiatedById != :userId ")
    Page<RescheduleRequest> findForClientAndStatus(@Param("clientId") Long clientId,
            @Param("userId") Long userId, @Param("status") RescheduleStatus status, Pageable pageable);

    Boolean existsBySession_IdAndStatus(Long sessionId, RescheduleStatus status);
}
