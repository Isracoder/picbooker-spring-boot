package com.example.picbooker.session;

import static java.util.Objects.isNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.picbooker.client.Client;
import com.example.picbooker.deposit.Deposit;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.sessionType.SessionTypeName;
import com.example.picbooker.workhours.WorkHour;
import com.example.picbooker.workhours.WorkHourService;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private WorkHourService workHourService;

    private Integer defaultSlotLengthHours = 1;

    public Session create(LocalDate date, LocalTime startTime, LocalTime endTime, String location, Double totalPrice,
            Currency currency, SessionStatus status, Client client, Photographer photographer, Deposit deposit) {

        return new Session(null, date, startTime, endTime, location, totalPrice, currency, status, client, photographer,
                deposit);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public Session createAndSave(LocalDate date, LocalTime startTime, LocalTime endTime, String location,
            Double totalPrice,
            Currency currency, SessionStatus status, Client client, Photographer photographer, Deposit deposit) {
        return save(create(date, startTime, endTime, location, totalPrice, currency, status, client, photographer,
                deposit));
    }

    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    public Session findByIdThrow(Long id) {
        return sessionRepository.findById(id).orElseThrow();
    }

    public Boolean hasAtLeastOneSlotOnDayOfLength(Long photographerId, LocalDate date, Integer slotLengthHours) {
        WorkHour workHour = workHourService.findForPhotographerAndDay(photographerId, DayOfWeek.from(date));
        if (isNull(workHour)
                || !(ChronoUnit.MINUTES.between(workHour.getStartTime(), workHour.getEndTime()) < slotLengthHours * 60))
            return false;
        List<Session> sessionsOnDay = sessionRepository.findByPhotographer_IdAndDateOrderByStartTimeAsc(photographerId,
                date);
        LocalTime previous = workHour.getStartTime();
        for (Session session : sessionsOnDay) {
            if (ChronoUnit.HOURS.between(previous, session.getStartTime()) >= slotLengthHours)
                return true;
            previous = session.getEndTime();
            // to do add buffer time ;
        }
        if (ChronoUnit.HOURS.between(previous, workHour.getEndTime()) >= slotLengthHours)
            return true;
        return false;
    }

    public Boolean canPhotographerHaveSessionOnDayBetween(Long photographerId, LocalDate date, LocalTime startTime,
            LocalTime endTime) {
        WorkHour workHour = workHourService.findForPhotographerAndDay(photographerId, DayOfWeek.from(date));
        if (isNull(workHour)
                || !(startTime.isAfter(workHour.getStartTime()) && endTime.isBefore(workHour.getEndTime())))
            return false;

        if (photographerHasSessionBetween(photographerId, date, startTime, endTime))
            return false;
        // to do check for blocks, buffer time
        return true;

    }

    // to think paginate
    public List<SessionSearchDTO> getPossibles(String city, SessionTypeName type, Double lowPrice, Double highPrice,
            LocalDate date, Boolean recommended) {
        // to do check nulls and return info
        return null;
    }

    // to think do I let him have 2 requests at the same time ?
    public Boolean photographerHasSessionBetween(Long photographerId, LocalDate date, LocalTime startTime,
            LocalTime endTime) {
        List<Session> sessions = sessionRepository.findBookedSessionsByPhotographer_IdAndDate(photographerId, date);
        Optional<Session> conflictingSession = sessions.stream()
                .filter(session -> (session.getStartTime().isBefore(endTime)
                        && session.getEndTime().isAfter(startTime)))
                // a session is conflicting if it starts before I end and ends after I
                // start
                .findFirst();
        return conflictingSession.isPresent();
    }

    public void cancelReservation(Long id) {
        // to do implement ;
    }

    public void addAddOnsToSessions(Long id) {
        // to do implement ;
    }

    // gets in one-hours slots
    // to think have one for specific session type length
    public List<AppointmentDTO> getAvailableAppointments(Long photographerId, LocalDate date, Integer slotLengthHours) {

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        slotLengthHours = Optional.ofNullable(slotLengthHours).orElse(defaultSlotLengthHours);
        WorkHour workhours = workHourService.findForPhotographerAndDay(photographerId, dayOfWeek);
        if (!isNull(workhours)) {
            List<AppointmentDTO> availableAppointments = new ArrayList<>();

            LocalTime startTime = workhours.getStartTime();
            LocalTime endTime = workhours.getEndTime();

            List<LocalTime> allTimeSlots = generateTimeSlots(startTime, endTime, slotLengthHours);

            List<Session> bookedSessions = sessionRepository.findBookedSessionsByPhotographer_IdAndDate(photographerId,
                    date);

            // Extract booked time slots
            List<LocalTime> bookedTimeSlots = bookedSessions.stream()
                    .map(Session::getStartTime)
                    .collect(Collectors.toList());

            for (LocalTime timeSlot : allTimeSlots) {
                if (!bookedTimeSlots.contains(timeSlot)) {
                    AppointmentDTO appointment = new AppointmentDTO();
                    // appointment.set(photographerId);
                    appointment.setDate(date);
                    appointment.setStartTime(timeSlot);
                    appointment.setEndTime(timeSlot.plusHours(slotLengthHours));
                    availableAppointments.add(appointment);
                }
            }

            return availableAppointments;
        }

        return Collections.emptyList();
    }

    private List<LocalTime> generateTimeSlots(LocalTime startTime, LocalTime endTime, Integer slotLengthHours) {
        List<LocalTime> timeSlots = new ArrayList<>();
        // LocalTime currentTime = startTime;
        while (endTime.isAfter(startTime)) {
            timeSlots.add(startTime);
            startTime.plusHours(slotLengthHours);
        }

        // while (currentTime.isBefore(endTime)) {
        // timeSlots.add(currentTime);
        // currentTime = currentTime.plusHours(1); // Assuming 1-hour slots
        // }

        return timeSlots;
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
