package com.example.picbooker.session;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findBookedSessionsByPhotographer_IdAndDate(Long photographerId, LocalDate date);

    // to check if there is redundancy
    List<Session> findByPhotographer_IdAndDateOrderByStartTimeAsc(Long photographerId, LocalDate date);

    // to think of having method for checking on date between 2 times
    // List<Session> findBookedSessionsByPhotographer_IdAndDate(Long photographerId,
    // LocalDate date);
}