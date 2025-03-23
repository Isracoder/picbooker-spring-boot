package com.example.picbooker.session.reschedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.session.Session;
import com.example.picbooker.session.SessionStatus;
import com.example.picbooker.system_message.EmailService;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;

public class RescheduleService {
    @Autowired
    private RescheduleRequestRepository rescheduleRequestRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void requestReschedule(Session session, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime) {

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
                // notificationService.notifyClient(session.getClient(), "Your session has been
                // rescheduled successfully.");
                return;
            }

            // Save request in DB for approval
            RescheduleRequest request = new RescheduleRequest(null, session, newDate, newStartTime, newEndTime,
                    RescheduleStatus.PENDING, LocalDateTime.now());
            rescheduleRequestRepository.save(request);

            // Notify photographer via email + in-app notification
            // notificationService.notifyPhotographer(photographer, "New reschedule request
            // pending approval.");
            emailService.sendGeneralEmail(session.getPhotographer().getUser().getEmail(),
                    "New Reschedule Request",
                    "A new rescheduling request is awaiting your approval.");
        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    @Transactional
    public void approveReschedule(Session session) {
        try {

            RescheduleRequest request = getRescheduleRequestOrThrow(session.getId());

            // Move session to new time and mark as approved
            session.setDate(request.getNewDate());
            session.setStartTime(request.getNewStartTime());
            session.setEndTime(request.getNewEndTime());
            // sessionRepository.save(session);

            // Update request status
            request.setStatus(RescheduleStatus.APPROVED);
            rescheduleRequestRepository.save(request);

            // Notify client
            // notificationService.notifyClient(session.getClient(), "Your session
            // reschedule has been approved.");
            emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                    "Reschedule successful",
                    "Your session has been rescheduled successfully to the date: " + request.getNewDate()
                            + " , and with the start time of: " + request.getNewStartTime());
            // to think delete reschedule request

        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    @Transactional
    public void rejectReschedule(Session session) {
        try {
            RescheduleRequest request = getRescheduleRequestOrThrow(session.getId());
            request.setStatus(RescheduleStatus.REJECTED);
            rescheduleRequestRepository.save(request);

            // Notify client
            // notificationService.notifyClient(request.getSession().getClient(), "Your
            // reschedule request was rejected.");
            emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                    "Reschedule successful",
                    "Your reschedule request was rejected. Try again with another time, communicate directly with the photographer, or choose to cancel your session.");
            // to think delete reschedule request
        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    @Transactional
    public void handleClientResponse(Long requestId, boolean accepted) {
        try {
            RescheduleRequest request = rescheduleRequestRepository.findById(requestId)
                    .orElseThrow(() -> new EntityNotFoundException("Reschedule request not found"));

            Session session = request.getSession();
            Photographer photographer = session.getPhotographer();

            if (accepted) {
                session.setDate(request.getNewDate());
                session.setStartTime(request.getNewStartTime());
                session.setEndTime(request.getNewEndTime());
                session.setStatus(SessionStatus.BOOKED);
                // sessionRepository.save(session);
                // notificationService.notifyPhotographer(photographer.getUser().getId(),
                // "Your reschedule request was accepted.");
                emailService.sendGeneralEmail(photographer.getUser().getEmail(), "Reschedule request accepted.",
                        "This email is to inform you that your rescheduling request has been approved by the client.\nGo to site for more info.");
            } else {
                // notificationService.notifyPhotographer(photographer.getUser().getId(),
                // "Your reschedule request was declined.");

                emailService.sendGeneralEmail(photographer.getUser().getEmail(), "Reschedule request decline.",
                        "This email is to inform you that your rescheduling request has been rejected by the client.\nGo to site for more info.");
            }

            // Delete the request after processing
            rescheduleRequestRepository.delete(request);
        } catch (MessagingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when notifying and sending emails.");
        }
    }

    private RescheduleRequest getRescheduleRequestOrThrow(Long sessionId) {
        return rescheduleRequestRepository.findBySessionIdAndStatus(sessionId, RescheduleStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("No pending reschedule request found."));
    }
}
