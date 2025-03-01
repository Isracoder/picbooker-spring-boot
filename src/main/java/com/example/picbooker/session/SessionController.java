package com.example.picbooker.session;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.sessionType.SessionTypeName;

// to think naming , sessions become appointments and use term bookings for booked ? 
// maybe use reservation 
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

        @Autowired
        private SessionService sessionService;

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
                                .status(HttpStatus.OK)
                                .build();

        }

        @PutMapping("/{id}/additional-services")
        public ApiResponse<String> addAddOnsToSession(@PathVariable("id") long sessionId) {
                // add additional services in reqbody
                // to do implement
                // sessionService.addAddOnsToSessions(sessionId);
                return ApiResponse.<String>builder()
                                .content("not implemented")
                                .status(HttpStatus.OK)
                                .build();

        }

        @PutMapping("/{id}/deposit")
        public ApiResponse<String> addDeposit(@PathVariable("id") long sessionId) {
                // add deposit info in req body : amount, currency, paidAt, method,
                // is this only for cash deposit ?
                return ApiResponse.<String>builder()
                                .content("not implemented")
                                .status(HttpStatus.OK)
                                .build();
        }

        // maybe add /:status to get past, upcoming, pending , etc (prob for bookings
        // not sessions)
        @GetMapping("/slots/{photographerId}/") // GET /api/sessions/123?date=2024-12-31
        public ApiResponse<?> getAvailableAppointments(
                        @PathVariable Long photographerId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

                // currently null ,
                sessionService.getAvailableAppointments(photographerId, date, null);

                return ApiResponse.<String>builder()
                                .content("Not implemented")
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

        @GetMapping("/possibles")
        public ApiResponse<List<SessionSearchDTO>> getPossiblesForSearch(@RequestParam("city") String city,
                        @RequestParam("type") String sessionType,
                        @RequestParam("date") LocalDate date, @RequestParam("lowPrice") Double lowPrice,
                        @RequestParam("highPrice") Double highPrice, @RequestParam("recommended") Boolean recommended) {

                // get possibles with params passed
                // city, type, price range, date
                List<SessionSearchDTO> searchResults = sessionService.getPossibles(city,
                                SessionTypeName.valueOf(sessionType),
                                lowPrice, highPrice, date,
                                recommended);
                return ApiResponse.<List<SessionSearchDTO>>builder()
                                .content(searchResults)
                                .status(HttpStatus.OK)
                                .build();

        }

}
