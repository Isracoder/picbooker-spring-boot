package com.example.picbooker.session;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

        List<Session> findBookedSessionsByPhotographer_IdAndDateAndStatus(Long photographerId, LocalDate date,
                        SessionStatus status);

        // to check if there is redundancy
        List<Session> findByPhotographer_IdAndDateOrderByStartTimeAsc(Long photographerId, LocalDate date);

        Page<Session> findByPhotographer_IdAndStatus(Long photographerId, SessionStatus status, Pageable pageable);

        Page<Session> findByPhotographer_IdAndStatusAndDateBefore(Long photographerId, SessionStatus status,
                        LocalDate date,
                        Pageable pageable);

        // Find sessions after a given date
        Page<Session> findByPhotographer_IdAndStatusAndDateAfter(Long photographerId, SessionStatus status,
                        LocalDate date,
                        Pageable pageable);

        // Find all sessions for a photographer after a certain date
        Page<Session> findByPhotographer_IdAndDateAfter(Long photographerId, LocalDate date, Pageable pageable);

        Page<Session> findByClient_IdAndStatus(Long photographerId, SessionStatus status, Pageable pageable);

        // Find sessions before a given date
        Page<Session> findByClient_IdAndStatusAndDateBefore(Long clientId, SessionStatus status, LocalDate date,
                        Pageable pageable);

        // Find sessions after a given date
        Page<Session> findByClient_IdAndStatusAndDateAfter(Long clientId, SessionStatus status, LocalDate date,
                        Pageable pageable);

        // Find all sessions for a client after a certain date
        Page<Session> findByClient_IdAndDateAfter(Long clientId, LocalDate date, Pageable pageable);

        // Check if a session exists before a given date
        Boolean existsByStatusAndClient_IdAndPhotographer_IdAndDateBefore(
                        SessionStatus status, Long clientId, Long photographerId, LocalDate date);
        // to think of having method for checking on date between 2 times
        // List<Session> findBookedSessionsByPhotographer_IdAndDate(Long photographerId,
        // LocalDate date);
}