package com.example.picbooker.session;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;

// to think naming , sessions become appointments and use term bookings for booked ? 
// maybe use reservation 
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/{id}")
    public ApiResponse<?> findById(@PathVariable("id") long bookingId) {
        try {
            // find a session by it's id get details
            sessionService.findById(bookingId);
            return ApiResponse.<String>builder()
                    .content("Not implemented")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content("Something went wrong :(")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @DeleteMapping("/{id}") // to do secure with conditions
    public ApiResponse<?> deleteById(@PathVariable("id") long bookingId) {
        try {
            // cancel a reservation
            // to do implement
            sessionService.cancelReservation(bookingId);
            return ApiResponse.<String>builder()
                    .content("not implemented")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content("Something went wrong :(")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/{id}/additional-services")
    public ApiResponse<String> addAdditionalServicesToSession(@PathVariable("id") long sessionId) {
        // add additional services in reqbody
        // to do implement
        sessionService.addAdditionalServicesToSessions(sessionId);
        return ApiResponse.<String>builder()
                .content("not implemented")
                .status(HttpStatus.OK)
                .build();

    }

    // maybe add /:status to get past, upcoming, pending , etc (prob for bookings
    // not sessions)
    @GetMapping("/{photographerId]/") // GET /api/sessions/123?date=2024-12-31
    public ApiResponse<?> getAvailableAppointments(
            @PathVariable Long photographerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            // get sessions for photographer
            // maybe have this get generated sessions based on workhours and on specific
            // date ,

            sessionService.getAvailableAppointments(photographerId, date);

            return ApiResponse.<String>builder()
                    .content("Not implemented")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content(e.getLocalizedMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/{photographerId]")
    public ApiResponse<?> getAvailableBetween(@PathVariable("photographerId") long id) {
        try {
            // get sessions for photographer
            // send between dates
            return ApiResponse.<String>builder()
                    .content("Not implemented")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content(e.getLocalizedMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

}
