package com.example.picbooker.session;

import static java.util.Objects.isNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.PageDTO;
import com.example.picbooker.blocked_time.BlockedTime;
import com.example.picbooker.client.Client;
import com.example.picbooker.client.ClientMapper;
import com.example.picbooker.client.ClientResponse;
import com.example.picbooker.deposit.Deposit;
import com.example.picbooker.deposit.DepositService;
import com.example.picbooker.deposit.DepositStatus;
import com.example.picbooker.deposit.PaymentMethod;
import com.example.picbooker.notification.NotificationService;
import com.example.picbooker.payments.PaymentProcessingService;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.photographer.PhotographerMapper;
import com.example.picbooker.photographer.PhotographerService;
import com.example.picbooker.photographer_additionalService.PhotographerAddOn;
import com.example.picbooker.photographer_additionalService.PhotographerAddOnService;
import com.example.picbooker.photographer_sessionType.PhotographerSessionType;
import com.example.picbooker.photographer_sessionType.PhotographerSessionTypeService;
import com.example.picbooker.session.reschedule.RescheduleAnswer;
import com.example.picbooker.session.reschedule.RescheduleDTO;
import com.example.picbooker.session.reschedule.RescheduleService;
import com.example.picbooker.session.reschedule.RescheduleStatus;
import com.example.picbooker.sessionType.SessionTypeName;
import com.example.picbooker.system_message.EmailService;
import com.example.picbooker.user.User;
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
    private RescheduleService rescheduleService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentProcessingService paymentProcessingService;

    @Autowired
    private PhotographerSessionTypeService photographerSessionTypeService;

    @Autowired
    private PhotographerAddOnService photographerAddOnService;

    private Integer defaultSlotLengthHours = 1;

    public Session create(LocalDate date, LocalTime startTime, LocalTime endTime, String location,
            String privateComment, Double totalPrice,
            Currency currency, SessionStatus status, Client client, String clientName, String clientEmail,
            Photographer photographer, Deposit deposit,
            PhotographerSessionType photographerSessionType, Set<PhotographerAddOn> photographerAddOns) {

        return new Session(null, date, startTime, endTime, location, privateComment, totalPrice, currency, clientName,
                clientEmail, status,
                client, photographerSessionType, photographer, deposit, photographerAddOns);

    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public Session createAndSave(LocalDate date, LocalTime startTime, LocalTime endTime, String location,
            String privateComment, String clientName, String clientEmail,
            Double totalPrice,
            Currency currency, SessionStatus status, Client client, Photographer photographer, Deposit deposit,
            PhotographerSessionType photographerSessionType, Set<PhotographerAddOn> photographerAddOns) {
        return save(create(date, startTime, endTime, location, privateComment, totalPrice, currency, status, client,
                clientName, clientEmail, photographer, deposit, photographerSessionType, photographerAddOns));
    }

    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    public Session findByIdThrow(Long id) {
        return sessionRepository.findById(id).orElseThrow();
    }

    // to page
    public PageDTO<SessionResponse> findByPhotographerAndStatus(Long photographerId, SessionStatus status,
            Pageable pageable) {
        Page<Session> page = sessionRepository.findByPhotographer_IdAndStatus(photographerId, status, pageable);
        List<SessionResponse> responses = (page.getContent().stream()
                .map(session -> toSessionResponse(session, session.getDeposit())).toList());
        return new PageDTO<SessionResponse>(responses, page.getTotalPages(), page.getTotalElements(), page.getNumber());
    }

    public Boolean hasAtLeastOneSlotOnDayOfLength(Long photographerId, LocalDate date, Integer slotLengthMinutes) {
        WorkHour workHour = workHourService.findForPhotographerAndDay(photographerId, DayOfWeek.from(date));
        // System.out.println(ChronoUnit.MINUTES.between(workHour.getStartTime(),
        // workHour.getEndTime()));
        if (isNull(workHour)
                || (ChronoUnit.MINUTES.between(workHour.getStartTime(), workHour.getEndTime()) < slotLengthMinutes))
            return false;
        // System.out.println("work hour has slot length");
        List<Session> sessionsOnDay = sessionRepository.findByPhotographer_IdAndDateOrderByStartTimeAsc(photographerId,
                date);
        LocalTime previous = workHour.getStartTime();
        for (Session session : sessionsOnDay) {
            if (ChronoUnit.MINUTES.between(previous, session.getStartTime()) >= slotLengthMinutes)
                return true;
            previous = session.getEndTime();
            // to do add buffer time ;
            // check request/deposit status
        }
        if (ChronoUnit.MINUTES.between(previous, workHour.getEndTime()) >= slotLengthMinutes)
            return true;
        // System.out.println("why am i false");
        return false;
    }

    public Boolean canPhotographerHaveSessionOnDayBetween(Long photographerId, LocalDate date, LocalTime startTime,
            LocalTime endTime) {
        WorkHour workHour = workHourService.findForPhotographerAndDay(photographerId, DayOfWeek.from(date));
        if (isNull(workHour)
                || (workHour.getStartTime().isAfter(startTime) || workHour.getEndTime().isBefore(endTime))) {
            System.out.println(workHour != null ? workHour.getId() : "null");
            System.out.println(startTime + " " + endTime);
            System.out.println("Not work hour");
            return false;
        }
        if (photographerHasSessionBetween(photographerId, date, startTime, endTime)) {
            System.out.println("Has Session");
            return false;
        }
        if (photographerHasBlockedTimeBetween(photographerId, date, startTime, endTime)) {
            System.out.println("Has block");
            return false;
        }
        // to do check for , buffer time, minimum
        return true;

    }

    public Boolean canPhotographerHaveCustomSessionOnDayBetween(Long photographerId, LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        if (photographerHasSessionBetween(photographerId, date, startTime, endTime)) {
            System.out.println("Has Session");
            return false;
        }

        return true;

    }

    public List<SessionSearchDTO> getTopPhotographersForSearchBySessionType(SessionTypeName type) {
        // to do implement paginated list of all top photographers for that type
        // ordered by rating
        // have other one without session type
        return null;
    }

    public List<SessionSearchDTO> getPossibles(String city, String country, SessionTypeName type, Double lowPrice,
            Double highPrice,
            LocalDate date) {
        List<SessionSearchDTO> results = new ArrayList<>();
        if (isNull(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Provide sufficient search parameters: session type, ...");
        }

        List<PhotographerSessionType> matchingSessions = new ArrayList<>();
        if (!isNull(city) && !city.isEmpty() && !city.isBlank()) {
            if (!isNull(country) && !country.isEmpty() && !country.isBlank()) {
                matchingSessions = photographerSessionTypeService
                        .findByPhotographerCityAndPhotographerCountryAndType(city, country, type);
            } else {

                matchingSessions = photographerSessionTypeService
                        .findByPhotographerCityAndType(city, type);
            }
            if (isNull(matchingSessions) || matchingSessions.size() == 0)
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
        if (!isNull(date)) {
            System.out.println("Before date filtering : " + matchingSessions.size());
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
                            : session.getCustomSessionType(),
                    session.getId()));

        }

        return results;
    }

    // to think do I let him have 2 requests at the same time ?
    public Boolean photographerHasSessionBetween(Long photographerId, LocalDate date, LocalTime startTime,
            LocalTime endTime) {
        List<Session> sessions = sessionRepository.findBookedSessionsByPhotographer_IdAndDateAndStatus(photographerId,
                date, SessionStatus.BOOKED);
        System.out.println(sessions.size());
        Optional<Session> conflictingSession = sessions.stream()
                .filter(session -> session.getStartTime().isBefore(endTime)
                        && session.getEndTime().isAfter(startTime))
                .findFirst();
        return conflictingSession.isPresent();
    }

    public Boolean photographerHasBlockedTimeBetween(Long photographerId, LocalDate date, LocalTime startTime,
            LocalTime endTime) {
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        List<BlockedTime> blocks = photographerService.findByPhotographerIdAndOverlapping(photographerId, startDateTime,
                endDateTime);

        Optional<BlockedTime> conflict = blocks.stream()
                .filter(block -> block.getStartDateTime().isBefore(endDateTime)
                        && block.getEndDateTime().isAfter(startDateTime))
                // conflict if block starts before I end && ends after I start
                .findFirst();

        return conflict.isPresent();
    }

    public void addAddOnsToSessions(Long id) {
        // to do implement ;
    }

    // to do continue, more payment logic,
    @Transactional
    public SessionResponse createBooking(SessionDTO sessionDTO, Client client) {
        try {

            if (isNull(client))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Client must not be null");

            // what if he changed default client info ?
            Photographer photographer = photographerService.findByIdThrow(sessionDTO.getPhotographerId());
            if (sessionDTO.getStartTime().isAfter(sessionDTO.getEndTime())
                    || !canPhotographerHaveSessionOnDayBetween(photographer.getId(), sessionDTO.getDate(),
                            sessionDTO.getStartTime(), sessionDTO.getEndTime())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Unavailable Time");
            }
            PhotographerSessionType photographerSessionType = photographerSessionTypeService
                    .findByIdThrow(sessionDTO.getPhotographerSessionTypeId());
            Set<PhotographerAddOn> photographerAddOns = sessionDTO.getPhotographerAddOnIds() != null
                    ? photographerAddOnService
                            .findSetByIds(sessionDTO.getPhotographerAddOnIds())
                    : new HashSet<>();
            // to do check that addons and session type are of same currency ;
            // maybe have currency fixed in p settings.
            Double addOnPrice = 0d;
            for (PhotographerAddOn addon : photographerAddOns) {
                addOnPrice += addon.getFee();
            }
            Double price = photographerSessionType.getPricePerDuration() + addOnPrice;

            Session session = createAndSave(sessionDTO.getDate(), sessionDTO.getStartTime(), sessionDTO.getEndTime(),
                    sessionDTO.getLocation(), sessionDTO.getPrivateComment(), sessionDTO.getClientName(),
                    sessionDTO.getClientEmail(), price,
                    photographerSessionType.getCurrency(),
                    SessionStatus.APPROVAL_PENDING, client, photographer, null, photographerSessionType,
                    photographerAddOns);
            Deposit deposit = null;
            if (photographerSessionType.getRequiresDeposit()) {

                deposit = depositService.createAndSave(session, photographerSessionType.getDepositAmount(),
                        photographerSessionType.getCurrency(), null, DepositStatus.UNPAID,
                        (sessionDTO.getPaymentMethod()), null);
                session.setDeposit(deposit);
            }

            String message = "Session Information: \n-Date: " + session.getDate().toString() + "\n-Time: "
                    + session.getStartTime() + "-" + session.getEndTime() + "\n-Type: "
                    + session.getSessionType().getType();

            notificationService.sendNotification(session.getClient().getUser(),
                    "Your session request has been sent!");

            emailService.sendGeneralEmail(client.getUser().getEmail(),
                    "Session Request Sent to Photographer Confirmation",
                    message);
            session = save(session);
            return toSessionResponse(session, deposit);
        } catch (Exception e) {
            System.out.println();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong: " + e.getLocalizedMessage());
        }

    }

    @Transactional
    public SessionResponse createCustomSession(CustomSessionDTO sessionDTO, Long photographerId) {
        try {

            Photographer photographer = photographerService.findByIdThrow(photographerId);

            if (sessionDTO.getStartTime().isAfter(sessionDTO.getEndTime())
                    || !canPhotographerHaveCustomSessionOnDayBetween(photographer.getId(), sessionDTO.getDate(),
                            sessionDTO.getStartTime(), sessionDTO.getEndTime())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Unavailable Time");
            }
            PhotographerSessionType photographerSessionType = photographerSessionTypeService
                    .findByIdThrow(sessionDTO.getPhotographerSessionTypeId());
            Set<PhotographerAddOn> photographerAddOns = sessionDTO.getPhotographerAddOnIds() != null
                    ? photographerAddOnService
                            .findSetByIds(sessionDTO.getPhotographerAddOnIds())
                    : new HashSet<>();
            Double addOnPrice = 0d;
            for (PhotographerAddOn addon : photographerAddOns) {
                addOnPrice += addon.getFee();
            }
            Double price = photographerSessionType.getPricePerDuration() + addOnPrice;

            Session session = createAndSave(sessionDTO.getDate(), sessionDTO.getStartTime(), sessionDTO.getEndTime(),
                    sessionDTO.getLocation(), sessionDTO.getPrivateComment(),
                    sessionDTO.getClientDetails().getPersonalName(), sessionDTO.getClientDetails().getEmail(), price,
                    photographerSessionType.getCurrency(),
                    SessionStatus.BOOKED, null, photographer, null, photographerSessionType,
                    photographerAddOns);
            Deposit deposit = null;
            if (photographerSessionType.getRequiresDeposit()) {

                deposit = depositService.createAndSave(session, photographerSessionType.getDepositAmount(),
                        photographerSessionType.getCurrency(), null, DepositStatus.UNPAID,
                        (sessionDTO.getPaymentMethod()), null);
                session.setDeposit(deposit);
            }

            String message = "Session Information: \n-Date: " + session.getDate().toString() + "\n-Time: "
                    + session.getStartTime() + "-" + session.getEndTime() + "\n-Type: "
                    + session.getSessionType().getType();

            // emailService.sendGeneralEmail(sessionDTO.getClientDetails().getEmail(),
            // "Private Session Booked Confirmation",
            // message);

            SessionResponse response = toSessionResponse(session, deposit);
            session = save(session);
            return response;
        } catch (Exception e) {
            System.out.println();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong: " + e.getLocalizedMessage());
        }
    }

    @Transactional
    public void clientReschedule(Client client, RescheduleDTO rescheduleDTO) {
        Session session = findByIdThrow(rescheduleDTO.getSessionId());
        Photographer photographer = session.getPhotographer();

        if (session.getClient().getId() != client.getId()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource.");
        }

        if (!canRescheduleWithInfo(photographer, session, rescheduleDTO)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Reschedule Request unable to be fulfilled.Only one at a time and valid requests permitted.");
        }

        rescheduleService.requestRescheduleAsClient(session, rescheduleDTO.getDate(), rescheduleDTO.getStartTime(),
                rescheduleDTO.getEndTime(), rescheduleDTO.getReason());

        // return toSessionResponse(session);
    }

    private Boolean canRescheduleWithInfo(Photographer photographer, Session session, RescheduleDTO rescheduleDTO) {
        if (session.getStatus() == SessionStatus.CANCELED || session.getDate().isBefore(LocalDate.now())) {
            // throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot reschedule a completed
            // or canceled session.");
            System.out.println("can't reschedule that session");
            return false;
        }

        if (rescheduleService.existsBySessionIdAndStatus(session.getId(), RescheduleStatus.PENDING)) {
            // throw new ApiException(HttpStatus.BAD_REQUEST, "One rescheduling request at a
            // time");
            System.out.println("there already is a request for that session");
            return false;
        }

        // Ensure request is before the photographer’s minimum notice time
        int minNoticeMinutes = photographer.getMinimumNoticeBeforeSessionMinutes();
        LocalDateTime newSessionStartTime = rescheduleDTO.getDate().atTime(rescheduleDTO.getStartTime());
        LocalDateTime latestAllowedRescheduleTime = newSessionStartTime.minusMinutes(minNoticeMinutes);

        if (LocalDateTime.now().isAfter(latestAllowedRescheduleTime)) {
            // throw new ApiException(HttpStatus.BAD_REQUEST,
            System.out.println("Rescheduling period has expired. A new session is required.");
            return false;
        }
        // Check availability before proceeding
        if (!canPhotographerHaveSessionOnDayBetween(photographer.getId(), rescheduleDTO.getDate(),
                rescheduleDTO.getStartTime(), rescheduleDTO.getEndTime())) {
            // throw new ApiException(HttpStatus.BAD_REQUEST, "Photographer is unavailable
            // at the requested time.");
            System.out.println("photographer unavailable at that time");
            return false;
        }
        return true;
    }

    @Transactional
    public SessionResponse photographerReschedule(Photographer photographer,
            RescheduleDTO rescheduleDTO) {

        Session session = findByIdThrow(rescheduleDTO.getSessionId());

        if (session.getPhotographer().getId() != photographer.getId()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource.");
        }

        if (!canRescheduleWithInfo(photographer, session, rescheduleDTO)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Reschedule Request unable to be fulfilled.Only one at a time and valid requests permitted.");
        }

        if (isNull(session.getClient())) {
            session.setDate(rescheduleDTO.getDate());
            session.setStartTime(rescheduleDTO.getStartTime());
            session.setEndTime(rescheduleDTO.getEndTime());

            return toSessionResponse(save(session), session.getDeposit());
        }
        rescheduleService.requestRescheduleAsPhotographer(session, rescheduleDTO.getDate(),
                rescheduleDTO.getStartTime(),
                rescheduleDTO.getEndTime(), rescheduleDTO.getReason());

        return toSessionResponse(session, session.getDeposit());

    }

    @Transactional
    public void photographerCancel(Long sessionId, Photographer photographer) {
        try {
            Session session = findByIdThrow(sessionId);
            if (LocalDate.now().isAfter(session.getDate())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Can't cancel a past booking");
            }
            if (session.getPhotographer().getId() != photographer.getId()) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }

            // to do add online Refund deposit to client
            // stripeConnectService.refundDeposit(session.getDepositId());

            // Mark session as canceled
            session.setStatus(SessionStatus.CANCELED);
            sessionRepository.save(session);

            // Track photographer cancellations
            // photographerPenaltyService.recordCancellation(photographerId);

            // Notify client
            String message = "Your session has been canceled by the photographer. If your deposit was paid online it has been refunded, if you paid in cash request it from the photographer.\nWe apologize for the inconvenience and will take steps to reduce this happening in the future.";
            if (!isNull(session.getClient())) {
                photographer.setCancellationStrikes(
                        isNull(photographer.getCancellationStrikes()) ? 0 : 1 + photographer.getCancellationStrikes());
                notificationService.sendNotification(session.getClient().getUser(),
                        "Your session has been canceled by the photographer");
                emailService.sendGeneralEmail(session.getClient().getUser().getEmail(), "Session CANCELLATION",
                        message);

            } else {
                emailService.sendGeneralEmail(session.getClientEmail(), "Session CANCELLATION", message);
            }
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while sending a message");
        }
    }

    @Transactional
    public void clientCancel(Long sessionId, Client client) {
        try {
            Session session = findByIdThrow(sessionId);
            if (LocalDate.now().isAfter(session.getDate())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Can't cancel a past booking");
            }
            if (session.getClient() == null || session.getClient().getId() != client.getId()) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }

            // Mark session as canceled (deposit is forfeited)
            session.setStatus(SessionStatus.CANCELED);
            sessionRepository.save(session);

            // Notify photographer
            notificationService.sendNotification(session.getPhotographer().getUser(),
                    "A Client has canceled their session.");

            emailService.sendGeneralEmail(session.getPhotographer().getUser().getEmail(), "Session Cancellation notice",
                    "A client has canceled their session.");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while sending a message");
        }
    }

    @Transactional
    public void processReschedulingAnswer(RescheduleAnswer rescheduleAnswer, User user) {

        if (rescheduleAnswer.getStatus() == RescheduleStatus.APPROVED) {
            rescheduleService.approveReschedule(findByIdThrow(rescheduleAnswer.getSessionId()), user);
        } else if (rescheduleAnswer.getStatus() == RescheduleStatus.APPROVED) {
            rescheduleService.rejectReschedule(findByIdThrow(rescheduleAnswer.getSessionId()), user);
        }

    }

    public static SessionResponse toSessionResponse(Session session, Deposit deposit) {
        String typeOrCustomType = session.getSessionType().getType() != null
                ? session.getSessionType().getType().toString()
                : session.getSessionType().getCustomSessionType();
        ClientResponse clientResponse = session.getClient() != null ? ClientMapper.toResponse(session.getClient())
                : null;
        return new SessionResponse(session.getId(), session.getSessionType().getId(),
                deposit != null ? deposit.getId() : null,
                PhotographerMapper.toResponse(session.getPhotographer()),
                session.getStatus(), deposit != null ? deposit.getStatus() : null,
                deposit != null ? deposit.getMethod() : null, session.getTotalPrice(),
                deposit != null ? deposit.getAmount() : null, session.getCurrency().getCurrencyCode(), typeOrCustomType,
                session.getClientName(),
                session.getClientEmail(), session.getDate(),
                session.getStartTime(), session.getEndTime(), session.getLocation(), session.getPrivateComment(),
                (session.getSessionAddOns().stream().map(addon -> addon.getId()).toList()),
                clientResponse);

    }

    public SessionDTO toSessionDTO(Session session) {
        return new SessionDTO(session.getPhotographer().getId(), session.getSessionType().getId(), session.getDate(),
                session.getStartTime(), session.getEndTime(), session.getLocation(), session.getClientName(),
                session.getClientEmail(), session.getPrivateComment(),
                session.getDeposit().getMethod(),
                (session.getSessionAddOns()).stream().map(addon -> addon.getId()).toList());
    }

    // gets in one-hours slots
    // to think have one for specific session type length
    public List<AppointmentDTO> getAvailableAppointments(Long sessionTypeID, LocalDate date) {
        // to think what if other ? then multiple types , have default slot length to be
        // what ?
        System.out.println(sessionTypeID + " , " + date.toString());
        PhotographerSessionType photographerSessionType = photographerSessionTypeService.findByIdThrow(sessionTypeID);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Integer slotLengthMinutes = photographerSessionType.getDurationMinutes();
        System.out.println(photographerSessionType.getId() + " " + dayOfWeek);
        WorkHour workHours = workHourService
                .findForPhotographerAndDay(photographerSessionType.getPhotographer().getId(), dayOfWeek);
        if (!isNull(workHours)) {
            System.out.println("workhours not null");
            List<AppointmentDTO> availableAppointments = new ArrayList<>();

            LocalTime startTime = workHours.getStartTime();
            LocalTime endTime = workHours.getEndTime();

            List<LocalTime> allTimeSlots = generateTimeSlots(startTime, endTime, slotLengthMinutes,
                    photographerSessionType.getPhotographer().getBufferTimeMinutes());
            System.out.println("Time slots generated with length: " + allTimeSlots.size());
            List<Session> bookedSessions = sessionRepository.findBookedSessionsByPhotographer_IdAndDateAndStatus(
                    photographerSessionType.getPhotographer().getId(),
                    date, SessionStatus.BOOKED);

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
                    appointment.setLocation(photographerSessionType.getLocation());
                    appointment.setSessionType(photographerSessionType.getType() != SessionTypeName.OTHER
                            ? photographerSessionType.getType().toString()
                            : photographerSessionType.getCustomSessionType());
                }
            }

            return availableAppointments;
        }

        return Collections.emptyList();
    }

    private List<LocalTime> generateTimeSlots(LocalTime startTime, LocalTime endTime, Integer slotLengthMinutes,
            Integer bufferTimeMinutes) {
        List<LocalTime> timeSlots = new ArrayList<>();
        if (isNull(bufferTimeMinutes))
            bufferTimeMinutes = 15;
        int cnt = 0;
        while (endTime.isAfter(startTime.plusMinutes(slotLengthMinutes - 1 + bufferTimeMinutes)) && cnt < 50) {
            cnt++;
            timeSlots.add(startTime);
            startTime = startTime.plusMinutes(slotLengthMinutes + bufferTimeMinutes);
        }

        return timeSlots;
    }

    @Transactional
    public void approveSessionRequest(Long sessionId, Long photographerId) {
        try {
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            if (session.getPhotographer().getId() != photographerId) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }
            session.setStatus(SessionStatus.BOOKED);
            sessionRepository.save(session);

            if (!isNull(session.getDeposit()) && session.getDeposit().getMethod() != PaymentMethod.CASH) {

                System.out.println("Send card payment link");
                String paymentLink = paymentProcessingService.createPaymentPageForDeposit(session.getDeposit(), session,
                        session.getPhotographer());

                emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                        "Session Approved",
                        "Your Session Request was approved by the photographer.\nPlease pay your deposit here: "
                                + paymentLink
                                + "\n\nIf you do not pay the deposit the photographer has the right to cancel your session.");
            } else {

                emailService.sendGeneralEmail(session.getClient().getUser().getEmail(),
                        "Session Approved",
                        "Your Session Request was approved by the photographer.\nPlease pay your deposit in accordance to what was agreed upon."
                                + "\n\nIf you do not pay the deposit the photographer has the right to cancel your session.");
            }
            notificationService.sendNotification(session.getClient().getUser(),
                    "Your session request has been approved!");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional
    public void confirmSessionDepositPayment(Long sessionId) {
        try {

            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            session.getDeposit().setStatus(DepositStatus.PAID);
            session.getDeposit().setPaidAt(LocalDateTime.now());
            sessionRepository.save(session);

            // Notify the client and photographer
            if (!isNull(session.getClient())) {

                emailService.sendGeneralEmail(session.getClient().getUser().getEmail(), "Session Deposit Confirmed",
                        "Your deposit has been confirmed!");
                notificationService.sendNotification(session.getClient().getUser(),
                        "Your session deposit has been confirmed!");
            } else {
                emailService.sendGeneralEmail(session.getClientEmail(), "Session Deposit Confirmed",
                        "Your session deposit has been confirmed!");
            }
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional
    public void failSessionDepositPayment(Long sessionId) {
        try {
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            session.getDeposit().setStatus(DepositStatus.UNPAID);
            sessionRepository.save(session);

            // Notify the client
            if (!isNull(session.getClient())) {

                emailService.sendGeneralEmail(session.getClient().getUser().getEmail(), "Payment Failed",
                        "Your payment failed. Please try again.");
                notificationService.sendNotification(session.getClient().getUser(),
                        "Payment failed");

            } else {
                emailService.sendGeneralEmail(session.getClientEmail(), "Payment Failed",
                        "Your payment failed. Please try again.");
            }
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional
    public void payCashDeposit(Long sessionId, Long photographerId) {
        Session session = findByIdThrow(sessionId);
        if (session.getPhotographer().getId() != photographerId) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
        }
        if (session.getDeposit().getStatus() == DepositStatus.PAID) {
            return;
        }
        session.getDeposit().setStatus(DepositStatus.PAID);
        session.getDeposit().setPaidAt(LocalDateTime.now());
        session.getDeposit().setMethod(PaymentMethod.CASH);
        save(session);
    }

    @Transactional
    public void changeSessionStatus(Long sessionId, Long photographerId, SessionStatus newSessionStatus) {
        Session session = findByIdThrow(sessionId);
        if (session.getPhotographer().getId() != photographerId) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
        }
        if (newSessionStatus == SessionStatus.BOOKED && session.getStatus() == SessionStatus.APPROVAL_PENDING) {
            approveSessionRequest(sessionId, photographerId);
        } else if (newSessionStatus == SessionStatus.REFUSED && session.getStatus() == SessionStatus.APPROVAL_PENDING) {
            session.setStatus(newSessionStatus);
            // to do check that refusing the session doesn't interfere with search,
            // should I cancel instead ?
        } else if (newSessionStatus == SessionStatus.APPROVAL_PENDING && session.getStatus() == SessionStatus.BOOKED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Can't change back to pending after approval");
        }
        save(session);
    }

    public PageDTO<SessionResponse> getUpcomingSessionsForClientWhereStatusAndAfter(Long clientId,
            SessionStatus status, LocalDate date, Pageable pageable) {
        date = Optional.ofNullable(date).orElse(LocalDate.now());
        if (!isNull(status)) {
            Page<Session> results = sessionRepository.findByClient_IdAndStatusAndDateAfter(clientId, status, date,
                    pageable);

            List<SessionResponse> responses = results.getContent().stream()
                    .map(session -> toSessionResponse(session, session.getDeposit()))
                    .collect(Collectors.toList());
            return new PageDTO<SessionResponse>(responses, results.getTotalPages(), results.getTotalElements(),
                    results.getNumber());
        } else {
            Page<Session> results = sessionRepository.findByClient_IdAndDateAfter(clientId, date, pageable);

            List<SessionResponse> responses = results.getContent().stream()
                    .map(session -> toSessionResponse(session, session.getDeposit()))
                    .collect(Collectors.toList());
            return new PageDTO<SessionResponse>(responses, results.getTotalPages(), results.getTotalElements(),
                    results.getNumber());
        }
    }

    public PageDTO<SessionResponse> getPastForClient(Long clientId, Pageable pageable) {
        Page<Session> results = sessionRepository.findByClient_IdAndStatusAndDateBefore(clientId,
                SessionStatus.BOOKED, LocalDate.now(), pageable);

        List<SessionResponse> responses = results.getContent().stream()
                .map(session -> toSessionResponse(session, session.getDeposit()))
                .collect(Collectors.toList());
        return new PageDTO<SessionResponse>(responses, results.getTotalPages(), results.getTotalElements(),
                results.getNumber());
    }

    public PageDTO<SessionResponse> getUpcomingSessionsForPhotographerWhereStatusAndAfter(Long photographerId,
            SessionStatus status, LocalDate date, Pageable pageable) {
        date = Optional.ofNullable(date).orElse(LocalDate.now());
        if (!isNull(status)) {
            Page<Session> results = sessionRepository.findByPhotographer_IdAndStatusAndDateAfter(photographerId, status,
                    date,
                    pageable);

            List<SessionResponse> responses = results.getContent().stream()
                    .map(session -> toSessionResponse(session, session.getDeposit()))
                    .collect(Collectors.toList());
            return new PageDTO<SessionResponse>(responses, results.getTotalPages(), results.getTotalElements(),
                    results.getNumber());
        } else {
            Page<Session> results = sessionRepository.findByPhotographer_IdAndDateAfter(photographerId, date, pageable);

            List<SessionResponse> responses = results.getContent().stream()
                    .map(session -> toSessionResponse(session, session.getDeposit()))
                    .collect(Collectors.toList());
            return new PageDTO<SessionResponse>(responses, results.getTotalPages(), results.getTotalElements(),
                    results.getNumber());
        }
    }

    public PageDTO<SessionResponse> getPastForPhotographer(Long photographerId, Pageable pageable) {
        Page<Session> results = sessionRepository.findByPhotographer_IdAndStatusAndDateBefore(photographerId,
                SessionStatus.BOOKED, LocalDate.now(), pageable);

        List<SessionResponse> responses = results.getContent().stream()
                .map(session -> toSessionResponse(session, session.getDeposit()))
                .collect(Collectors.toList());
        return new PageDTO<SessionResponse>(responses, results.getTotalPages(), results.getTotalElements(),
                results.getNumber());
    }

}
