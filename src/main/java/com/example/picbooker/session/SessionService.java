package com.example.picbooker.session;

import static java.util.Objects.isNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.picbooker.workhours.WorkHour;
import com.example.picbooker.workhours.WorkHourService;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private WorkHourService workHourService;

    private Integer sessionLengthHours;

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

    public List<AppointmentDTO> getAvailableAppointments(Long photographerId, LocalDate date) {

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        WorkHour workhours = workHourService.findForPhotographerAndDay(photographerId, dayOfWeek);
        if (!isNull(workhours)) {
            List<AppointmentDTO> availableAppointments = new ArrayList<>();

            Integer startTime = workhours.getStartHour();
            Integer endTime = workhours.getEndHour();

            List<Integer> allTimeSlots = generateTimeSlots(startTime, endTime);

            List<Session> bookedSessions = sessionRepository.findBookedSessionsByPhotographer_IdAndDate(photographerId,
                    date);

            // Extract booked time slots
            List<LocalTime> bookedTimeSlots = bookedSessions.stream()
                    .map(Session::getStartTime)
                    .collect(Collectors.toList());

            for (Integer timeSlot : allTimeSlots) {
                if (!bookedTimeSlots.contains(LocalTime.of(timeSlot, 0))) {
                    AppointmentDTO appointment = new AppointmentDTO();
                    // appointment.set(photographerId);
                    appointment.setDate(date);
                    appointment.setStartTime(LocalTime.of(timeSlot, 0));
                    appointment.setEndTime(LocalTime.of(timeSlot + sessionLengthHours, 0));
                    availableAppointments.add(appointment);
                }
            }

            return availableAppointments;
        }

        return Collections.emptyList();
    }

    private List<Integer> generateTimeSlots(Integer startTime, Integer endTime) {
        List<Integer> timeSlots = new ArrayList<>();
        // LocalTime currentTime = startTime;
        while (startTime < endTime) {
            timeSlots.add(startTime);
            startTime += sessionLengthHours;
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
