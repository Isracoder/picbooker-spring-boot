package com.example.picbooker.session;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.client.ClientService;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.sessionType.SessionTypeName;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

// to think naming , sessions become appointments and use term bookings for booked ? 
// maybe use reservation 
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

        @Autowired
        private SessionService sessionService;

        @Autowired
        private ClientService clientService;

        @GetMapping("/{id}")
        public ApiResponse<?> findById(@PathVariable("id") long bookingId) {

                // find a session by it's id get details
                sessionService.findById(bookingId);
                return ApiResponse.<String>builder()
                                .content("Not implemented")
                                .status(HttpStatus.OK)
                                .build();

        }

        @DeleteMapping("/{id}") // to do secure with conditions
        public ApiResponse<?> deleteById(@PathVariable("id") long bookingId) {

                // cancel a reservation
                // to do implement
                sessionService.cancelReservation(bookingId);
                return ApiResponse.<String>builder()
                                .content("not implemented")
                                .status(HttpStatus.NOT_IMPLEMENTED)
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
        public ApiResponse<String> addDeposit(@PathVariable("sessionId") long sessionId) {
                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

                sessionService.payCashDeposit(sessionId, photographer.getId());
                return ApiResponse.<String>builder()
                                .content("Success")
                                .status(HttpStatus.OK)
                                .build();
        }

        @PatchMapping("/{sessionId}/status")
        public ApiResponse<String> modifyBooking(@PathVariable("sessionId") long sessionId,
                        @RequestBody SessionStatus sessionStatus) {
                Photographer photographer = UserService
                                .getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

                sessionService.changeSessionStatus(sessionId, photographer.getId(), sessionStatus);
                return ApiResponse.<String>builder()
                                .content("Success. New status: " + sessionStatus)
                                .status(HttpStatus.OK)
                                .build();
        }

        @PostMapping("/booking")
        public ApiResponse<String> addBookingRequest(@RequestBody SessionDTO sessionRequest) {
                // add deposit info in req body : amount, currency, paidAt, method,
                // is this only for cash deposit ?
                User user = UserService.getLoggedInUserThrow();

                sessionService.createBooking(sessionRequest, user.getClient());
                // to do return booking info maybe
                return ApiResponse.<String>builder()
                                .content("Success")
                                .status(HttpStatus.OK)
                                .build();
        }

        // @PatchMapping("/booking/{bookingId}")
        // public ApiResponse<String> modifyBooking(@PathVariable("bookingId") long
        // bookingId) {
        // // should I split for photographer and client ? maybe this for client
        // // client adds services later on, comment/request
        // return ApiResponse.<String>builder()
        // .content("not implemented")
        // .status(HttpStatus.OK)
        // .build();
        // }

        // maybe add /:status to get past, upcoming, pending , etc (prob for bookings
        // not sessions)
        @GetMapping("/slots/{sessionTypeId}/") // GET /api/sessions/slots/123?date=2024-12-31
        public ApiResponse<List<AppointmentDTO>> getAvailableAppointments(
                        @PathVariable Long sessionTypeId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

                // currently null ,
                List<AppointmentDTO> appointments = sessionService.getAvailableAppointments(sessionTypeId, date);

                return ApiResponse.<List<AppointmentDTO>>builder()
                                .content(appointments)
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/{photographerId}")
        public ApiResponse<?> getAvailableBetween(@PathVariable("photographerId") long id) {

                // get sessions for photographer
                // send between dates
                return ApiResponse.<String>builder()
                                .content("Not implemented")
                                .status(HttpStatus.OK)
                                .build();

        }

        @GetMapping("/possibles") // maybe get photographers ?
        public ApiResponse<List<SessionSearchDTO>> getPossiblesForSearch(@RequestParam("city") String city,
                        @RequestParam("type") String sessionType,
                        @RequestParam("date") LocalDate date,
                        @RequestParam(name = "minPrice", required = false) Double minPrice,
                        @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                        @RequestParam(name = "recommended", defaultValue = "false") Boolean recommended) {

                // get possibles with params passed
                // city, type, price range, date
                List<SessionSearchDTO> searchResults = sessionService.getPossibles(city,
                                SessionTypeName.valueOf(sessionType),
                                minPrice, maxPrice, date);
                return ApiResponse.<List<SessionSearchDTO>>builder()
                                .content(searchResults)
                                .status(HttpStatus.OK)
                                .build();

        }

}
