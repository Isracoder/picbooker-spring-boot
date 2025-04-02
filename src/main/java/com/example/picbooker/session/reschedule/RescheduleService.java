package com.example.picbooker.session.reschedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.notification.NotificationService;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.session.Session;
import com.example.picbooker.session.SessionStatus;
import com.example.picbooker.system_message.EmailService;
import com.example.picbooker.user.User;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class RescheduleService {
    @Autowired
    private RescheduleRequestRepository rescheduleRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void requestRescheduleAsClient(Session session, LocalDate newDate, LocalTime newStartTime,
            LocalTime newEndTime) {

        try {
            Photographer photographer = session.getPhotographer();

            // If session is still pending, reschedule directly
            if (session.getStatus() == SessionStatus.APPROVAL_PENDING) {
                session.setDate(newDate);
                session.setStartTime(newStartTime);
                session.setEndTime(newEndTime);
                // sessionRepository.save(session);
                emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                        "Reschedule successful",
                        "Your session has been rescheduled successfully to the date: " + newDate
                                + " , and with the start time of: " + newStartTime);
                notificationService.sendNotification(session.getClient().getUser(),
                        "Your session has been rescheduled successfully.");
                return;
            }

            // Save request in DB for approval
            RescheduleRequest request = new RescheduleRequest(null, session.getClient().getUser().getId(), session,
                    newDate, newStartTime, newEndTime,
                    RescheduleStatus.PENDING, LocalDateTime.now());

            rescheduleRequestRepository.save(request);

            // Notify photographer via email + in-app notification
            notificationService.sendNotification(photographer.getUser(), "New reschedule request pending approval.");
            emailService.sendGeneralEmail(session.getPhotographer().getUser().getEmail(),
                    "New Reschedule Request",
                    "A new rescheduling request is awaiting your approval.");
        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    @Transactional
    public void requestRescheduleAsPhotographer(Session session, LocalDate newDate, LocalTime newStartTime,
            LocalTime newEndTime) {

        try {

            // Save request in DB for approval
            RescheduleRequest request = new RescheduleRequest(null, session.getPhotographer().getUser().getId(),
                    session,
                    newDate, newStartTime, newEndTime,
                    RescheduleStatus.PENDING, LocalDateTime.now());

            rescheduleRequestRepository.save(request);

            // Notify client via email + in-app notification
            notificationService.sendNotification(session.getClient().getUser(),
                    "New reschedule request pending approval.");
            emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                    "New Reschedule Request",
                    "A new rescheduling request is awaiting your approval.");
        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    @Transactional
    public void approveReschedule(Session session, User user) {
        try {

            RescheduleRequest request = getRescheduleRequestOrThrow(session.getId());
            if ((user.getId() != session.getPhotographer().getId() && user.getId() != session.getClient().getId())
                    || (user.getId() == request.getInitiatedById())) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }

            // Move session to new time and mark as approved
            session.setDate(request.getNewDate());
            session.setStartTime(request.getNewStartTime());
            session.setEndTime(request.getNewEndTime());
            // sessionRepository.save(session);

            // Update request status
            request.setStatus(RescheduleStatus.APPROVED);
            // to think delete reschedule request
            rescheduleRequestRepository.save(request);

            // Notify client
            notificationService.sendNotification(session.getClient().getUser(),
                    "Your session reschedule has been approved.");
            notificationService.sendNotification(session.getPhotographer().getUser(),
                    "Your session reschedule has been approved.");
            emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                    "Reschedule successful",
                    "Your session has been rescheduled successfully to the date: " + request.getNewDate()
                            + " , and with the start time of: " + request.getNewStartTime());

            emailService.sendGeneralEmail(session.getPhotographer().getUser().getEmail(),
                    "Reschedule successful",
                    "One of your sessions has been rescheduled successfully to the date: " + request.getNewDate()
                            + " , and with the start time of: " + request.getNewStartTime());

        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    @Transactional
    public void rejectReschedule(Session session, User user) {
        try {
            RescheduleRequest request = getRescheduleRequestOrThrow(session.getId());
            if ((user.getId() != session.getPhotographer().getId() && user.getId() != session.getClient().getId())
                    || (user.getId() == request.getInitiatedById())) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }

            request.setStatus(RescheduleStatus.REJECTED);
            // to think delete reschedule request
            rescheduleRequestRepository.save(request);

            // Notify
            notificationService.sendNotification(request.getSession().getClient().getUser(),
                    "Your reschedule request was rejected.");
            notificationService.sendNotification(request.getSession().getPhotographer().getUser(),
                    "Your reschedule request was rejected.");
            emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                    "Reschedule successful",
                    "Your reschedule request was rejected. Try again with another time, communicate directly with the photographer, or choose to cancel your session.");
        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    private RescheduleRequest getRescheduleRequestOrThrow(Long sessionId) {
        return rescheduleRequestRepository.findBySessionIdAndStatus(sessionId, RescheduleStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("No pending reschedule request found."));
    }

    public boolean existsBySessionIdAndStatus(Long sessionId, RescheduleStatus status) {
        return rescheduleRequestRepository.existsBySession_IdAndStatus(sessionId, status);
    }

}
