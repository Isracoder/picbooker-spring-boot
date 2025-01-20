package com.example.picbooker.session;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public void create() {
        // to do implement ;
    }

    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    public Session findByIdThrow(Long id) {
        return sessionRepository.findById(id).orElseThrow();
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public void cancelReservation(Long id) {
        // to do implement ;
    }

    public void addAdditionalServicesToSessions(Long id) {
        // to do implement ;
    }

    public void getAvailableAppointments(Long photographerId, LocalDate date) {
        // to do implement ;
    }

    public void blockOutTime(Long photographerId) {
        // to do implement ;
        // date and time
    }

    public void createPrivateSession(Long photographerId) {
        // to do implement ;
        // generate it as link ?
    }

    // gener

}
