package com.example.picbooker.session;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.PageDTO;
import com.example.picbooker.client.Client;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.session.reschedule.RescheduleAnswer;
import com.example.picbooker.session.reschedule.RescheduleDTO;
import com.example.picbooker.session.reschedule.RescheduleRequestResponse;
import com.example.picbooker.session.reschedule.RescheduleService;
import com.example.picbooker.session.reschedule.RescheduleStatus;
import com.example.picbooker.sessionType.SessionTypeName;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

// to think naming , sessions become appointments and use term bookings for booked ? 
// maybe use reservation 
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

        @Autowired
        private SessionService sessionService;

        @Autowired
        private RescheduleService rescheduleService;

        @GetMapping("/{id}")
        public ApiResponse<?> findById(@PathVariable("id") long bookingId) {

                // find a session by it's id get details
                sessionService.findById(bookingId);
                return ApiResponse.<String>builder()
                                .content("Not implemented")
                                .status(HttpStatus.OK)
                                .build();

        }

        @PutMapping("/{sessionId}/additional-services")
        public ApiResponse<String> addAddOnsToSession(@PathVariable("sessionId") long sessionId) {
                // add additional services in reqbody
                // to do implement
                // sessionService.addAddOnsToSessions(sessionId);
                return ApiResponse.<String>builder()
                                .content("not implemented")
                                .status(HttpStatus.OK)
                                .build();

        }

        @PutMapping("/{sessionId}/cash-deposit")
        public ApiResponse<Map<String, String>> addDeposit(@PathVariable("sessionId") long sessionId) {
                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

                sessionService.payCashDeposit(sessionId, photographer.getId());
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PatchMapping("/{sessionId}/status")
        public ApiResponse<Map<String, String>> modifyBooking(@PathVariable("sessionId") long sessionId,
                        @RequestBody Map<String, SessionStatus> body) {
                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

                sessionService.changeSessionStatus(sessionId, photographer.getId(), body.get("status"));
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("newStatus", body.get("status").toString()))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PostMapping("/booking")
        public ApiResponse<SessionResponse> addBookingRequest(@RequestBody SessionDTO sessionRequest) {

                User user = UserService.getLoggedInUserThrow();

                SessionResponse sessionResponse = sessionService.createBooking(sessionRequest,
                                UserService.getClientFromUserThrow(user));
                return ApiResponse.<SessionResponse>builder()
                                .content(sessionResponse)
                                .status(HttpStatus.OK)
                                .build();
        }

        @PostMapping("/custom-booking")
        public ApiResponse<SessionResponse> createCustomAppointment(@RequestBody CustomSessionDTO sessionRequest) {

                User user = UserService.getLoggedInUserThrow();

                SessionResponse sessionResponse = sessionService.createCustomSession(sessionRequest,
                                UserService.getPhotographerFromUserThrow(user).getId());

                return ApiResponse.<SessionResponse>builder()
                                .content(sessionResponse)
                                .status(HttpStatus.OK)
                                .build();
        }

        @PutMapping("/reschedule/client")
        public ApiResponse<Map<String, String>> clientReschedule(
                        @RequestBody RescheduleDTO rescheduleDTO) {

                Client client = UserService.getClientFromUserThrow(UserService.getLoggedInUserThrow());
                sessionService.clientReschedule(client, rescheduleDTO);
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PutMapping("/reschedule/photographer")
        public ApiResponse<SessionResponse> photographerReschedule(

                        @RequestBody RescheduleDTO rescheduleDTO) {
                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());
                SessionResponse sessionResponse = sessionService.photographerReschedule(photographer,
                                rescheduleDTO);
                return ApiResponse.<SessionResponse>builder()
                                .content(sessionResponse)
                                .status(HttpStatus.OK)
                                .build();
        }

        @PutMapping("/reschedule/answer")
        public ApiResponse<Map<String, String>> processReschedulingResponse(
                        @RequestBody RescheduleAnswer response) {

                sessionService.processReschedulingAnswer(response, UserService.getLoggedInUserThrow());
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PutMapping("/{sessionId}/cancel/photographer")
        public ApiResponse<Map<String, String>> photographerCancel(
                        @PathVariable Long sessionId) {

                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());
                sessionService.photographerCancel(sessionId, photographer);
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PutMapping("/{sessionId}/cancel/client")
        public ApiResponse<Map<String, String>> clientCancel(
                        @PathVariable Long sessionId) {

                Client client = UserService
                                .getClientFromUserThrow(UserService.getLoggedInUserThrow());
                sessionService.clientCancel(sessionId, client);
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        // maybe add /:status to get past, upcoming, pending , etc (prob for bookings
        // not sessions)
        @GetMapping("/appointments/{sessionTypeId}") // GET /api/sessions/slots/123?date=2024-12-31
        public ApiResponse<List<AppointmentDTO>> getAvailableAppointments(
                        @PathVariable("sessionTypeId") Long sessionTypeId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

                // currently null ,
                System.out.println("In slots function");
                List<AppointmentDTO> appointments = sessionService.getAvailableAppointments(sessionTypeId, date);

                return ApiResponse.<List<AppointmentDTO>>builder()
                                .content(appointments)
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/possibles") // maybe get photographers ?
        public ApiResponse<List<SessionSearchDTO>> getPossiblesForSearch(
                        @RequestParam(name = "city", required = false) String city,
                        @RequestParam(name = "country", required = false) String country,
                        @RequestParam("type") String sessionType,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        @RequestParam(name = "minPrice", required = false) Double minPrice,
                        @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                        @RequestParam(name = "recommended", defaultValue = "false") Boolean recommended) {

                // get possibles with params passed
                // city, type, price range, date
                List<SessionSearchDTO> searchResults = sessionService.getPossibles(city, country,
                                SessionTypeName.valueOf(sessionType),
                                minPrice, maxPrice, date);
                return ApiResponse.<List<SessionSearchDTO>>builder()
                                .content(searchResults)
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/photographers")
        public ApiResponse<PageDTO<SessionResponse>> getPhotographerSessions(
                        @RequestParam(name = "status", defaultValue = "APPROVAL_PENDING") String status,
                        @PageableDefault(size = 10, direction = Direction.DESC, sort = "date") Pageable pageable) {

                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());
                // if
                PageDTO<SessionResponse> searchResults = sessionService.findByPhotographerAndStatus(
                                photographer.getId(), SessionStatus.valueOf(status), pageable);
                return ApiResponse.<PageDTO<SessionResponse>>builder()
                                .content(searchResults)
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/reschedule/photographers")
        public ApiResponse<PageDTO<RescheduleRequestResponse>> getPhotographerReschedulingRequests(
                        @RequestParam(name = "status", defaultValue = "PENDING") String status,
                        @PageableDefault(size = 10, direction = Direction.DESC, sort = "requestTimestamp") Pageable pageable) {

                User user = UserService.getLoggedInUserThrow();
                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(user);

                PageDTO<RescheduleRequestResponse> searchResults = rescheduleService.findByPhotographerAndStatus(
                                photographer.getId(), user.getId(), RescheduleStatus.valueOf(status), pageable);
                return ApiResponse.<PageDTO<RescheduleRequestResponse>>builder()
                                .content(searchResults)
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/reschedule/clients")
        public ApiResponse<PageDTO<RescheduleRequestResponse>> getClientReschedulingRequests(
                        @RequestParam(name = "status", defaultValue = "PENDING") String status,
                        @PageableDefault(size = 10, direction = Direction.DESC, sort = "requestTimestamp") Pageable pageable) {

                User user = UserService.getLoggedInUserThrow();
                Client client = UserService
                                .getClientFromUserThrow(user);

                PageDTO<RescheduleRequestResponse> searchResults = rescheduleService.findByClientAndStatus(
                                client.getId(), user.getId(), RescheduleStatus.valueOf(status), pageable);
                return ApiResponse.<PageDTO<RescheduleRequestResponse>>builder()
                                .content(searchResults)
                                .status(HttpStatus.OK)
                                .build();

        }

}
