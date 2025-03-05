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
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.client.Client;
import com.example.picbooker.deposit.Deposit;
import com.example.picbooker.deposit.DepositService;
import com.example.picbooker.deposit.DepositStatus;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.photographer.PhotographerMapper;
import com.example.picbooker.photographer.PhotographerService;
import com.example.picbooker.photographer_additionalService.PhotographerAddOn;
import com.example.picbooker.photographer_additionalService.PhotographerAddOnService;
import com.example.picbooker.photographer_sessionType.PhotographerSessionType;
import com.example.picbooker.photographer_sessionType.PhotographerSessionTypeService;
import com.example.picbooker.sessionType.SessionTypeName;
import com.example.picbooker.workhours.WorkHour;
import com.example.picbooker.workhours.WorkHourService;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private WorkHourService workHourService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private PhotographerService photographerService;

    @Autowired
    private PhotographerSessionTypeService photographerSessionTypeService;

    @Autowired
    private PhotographerAddOnService photographerAddOnService;

    private Integer defaultSlotLengthHours = 1;

    public Session create(LocalDate date, LocalTime startTime, LocalTime endTime, String location,
            String privateComment, Double totalPrice,
            Currency currency, SessionStatus status, Client client, Photographer photographer, Deposit deposit,
            PhotographerSessionType photographerSessionType, Set<PhotographerAddOn> photographerAddOns) {

        return new Session(null, date, startTime, endTime, location, privateComment, totalPrice, currency, status,
                client, photographerSessionType, photographer, deposit, photographerAddOns);

    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public Session createAndSave(LocalDate date, LocalTime startTime, LocalTime endTime, String location,
            String privateComment,
            Double totalPrice,
            Currency currency, SessionStatus status, Client client, Photographer photographer, Deposit deposit,
            PhotographerSessionType photographerSessionType, Set<PhotographerAddOn> photographerAddOns) {
        return save(create(date, startTime, endTime, location, privateComment, totalPrice, currency, status, client,
                photographer,
                deposit, photographerSessionType, photographerAddOns));
    }

    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    public Session findByIdThrow(Long id) {
        return sessionRepository.findById(id).orElseThrow();
    }

    public Boolean hasAtLeastOneSlotOnDayOfLength(Long photographerId, LocalDate date, Integer slotLengthMinutes) {
        WorkHour workHour = workHourService.findForPhotographerAndDay(photographerId, DayOfWeek.from(date));
        if (isNull(workHour)
                || !(ChronoUnit.MINUTES.between(workHour.getStartTime(), workHour.getEndTime()) < slotLengthMinutes))
            return false;
        List<Session> sessionsOnDay = sessionRepository.findByPhotographer_IdAndDateOrderByStartTimeAsc(photographerId,
                date);
        LocalTime previous = workHour.getStartTime();
        for (Session session : sessionsOnDay) {
            if (ChronoUnit.MINUTES.between(previous, session.getStartTime()) >= slotLengthMinutes)
                return true;
            previous = session.getEndTime();
            // to do add buffer time ;
        }
        if (ChronoUnit.MINUTES.between(previous, workHour.getEndTime()) >= slotLengthMinutes)
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

    public List<SessionSearchDTO> getTopPhotographersForSearchBySessionType(SessionTypeName type) {
        // to do implement paginated list of all top photographers for that type
        // ordered by rating
        // have other one without session type
        return null;
    }

    public List<SessionSearchDTO> getPossibles(String city, SessionTypeName type, Double lowPrice, Double highPrice,
            LocalDate date) {
        List<SessionSearchDTO> results = new ArrayList<>();
        if (isNull(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Provide sufficient search parameters: session type, ...");
        }

        List<PhotographerSessionType> matchingSessions = new ArrayList<>();
        if (!isNull(city)) {

            matchingSessions = photographerSessionTypeService
                    .findByPhotographerCityAndType(city, type);
            if (isNull(matchingSessions))
                return results;
        } else
            matchingSessions = photographerSessionTypeService.findByType(type);

        // Apply price filter if given , keep to front
        // if (lowPrice != null || highPrice != null) {
        // matchingSessions = matchingSessions.stream()
        // .filter(session -> (lowPrice == null || session.getPrice() >= lowPrice) &&
        // (highPrice == null || session.getPrice() <= highPrice))
        // .collect(Collectors.toList());
        // }

        // if date is given, filter photographers who have availability
        if (date != null) {
            matchingSessions = matchingSessions.stream()
                    .filter(session -> hasAtLeastOneSlotOnDayOfLength(session.getPhotographer().getId(), date,
                            session.getDurationMinutes()))
                    .collect(Collectors.toList());
        }

        // Convert matching sessions into DTOs
        for (PhotographerSessionType session : matchingSessions) {
            results.add(new SessionSearchDTO(PhotographerMapper.toResponse(session.getPhotographer()), date,
                    session.getPricePerDuration(), session.getCurrency(), session.getDurationMinutes(),
                    session.getDepositAmount(),
                    session.getType() != SessionTypeName.OTHER ? session.getType().toString()
                            : session.getCustomSessionType()));
        }

        return results;
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

    // to do continue, more payment logic,
    @Transactional
    public void createBooking(SessionDTO sessionDTO, Client client) {
        if (isNull(client))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Client must not be null");
        // get necessary info: email, name, phone number
        // photographer, session type(length, cost, deposit)
        // what if he changed default client info ?
        Photographer photographer = photographerService.findByIdThrow(sessionDTO.getPhotographerId());
        PhotographerSessionType photographerSessionType = photographerSessionTypeService
                .findByIdThrow(sessionDTO.getPhotographerSessionTypeId());
        Set<PhotographerAddOn> photographerAddOns = photographerAddOnService
                .findSetByIds(sessionDTO.getPhotographerAddOnIds());
        // to do check that addons and session type are of same currency ;
        // maybe have currency fixed in p settings.
        Double addOnPrice = 0d;
        Double price = photographerSessionType.getPricePerDuration() + addOnPrice;

        Session session = createAndSave(sessionDTO.getDate(), sessionDTO.getStartTime(), sessionDTO.getEndTime(),
                sessionDTO.getLocation(), sessionDTO.getPrivateComment(), price, photographerSessionType.getCurrency(),
                SessionStatus.AWAITING_APPROVAL, client, photographer, null, photographerSessionType,
                photographerAddOns);
        if (photographerSessionType.getRequiresDeposit()) {

            Deposit deposit = depositService.createAndSave(session, photographerSessionType.getDepositAmount(),
                    photographerSessionType.getCurrency(), null, DepositStatus.UNPAID, (sessionDTO.getPaymentMethod()));
            session.setDeposit(deposit);
        }

    }

    // gets in one-hours slots
    // to think have one for specific session type length
    public List<AppointmentDTO> getAvailableAppointments(Long sessionTypeID, LocalDate date) {
        // to think what if other ? then multiple types , have default slot length to be
        // what ?
        PhotographerSessionType photographerSessionType = photographerSessionTypeService.findByIdThrow(sessionTypeID);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Integer slotLengthMinutes = photographerSessionType.getDurationMinutes();
        WorkHour workhours = workHourService
                .findForPhotographerAndDay(photographerSessionType.getPhotographer().getId(), dayOfWeek);
        if (!isNull(workhours)) {
            List<AppointmentDTO> availableAppointments = new ArrayList<>();

            LocalTime startTime = workhours.getStartTime();
            LocalTime endTime = workhours.getEndTime();

            List<LocalTime> allTimeSlots = generateTimeSlots(startTime, endTime, slotLengthMinutes);

            List<Session> bookedSessions = sessionRepository.findBookedSessionsByPhotographer_IdAndDate(
                    photographerSessionType.getPhotographer().getId(),
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
                    appointment.setEndTime(timeSlot.plusMinutes(slotLengthMinutes));
                    availableAppointments.add(appointment);
                }
            }

            return availableAppointments;
        }

        return Collections.emptyList();
    }

    private List<LocalTime> generateTimeSlots(LocalTime startTime, LocalTime endTime, Integer slotLengthMinutes) {
        List<LocalTime> timeSlots = new ArrayList<>();
        // LocalTime currentTime = startTime;
        while (endTime.isAfter(startTime)) {
            timeSlots.add(startTime);
            startTime.plusMinutes(slotLengthMinutes);
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
