package com.example.picbooker.photographer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;

@RestController
@RequestMapping("/api/photographers")
public class PhotographerController {

    @Autowired
    private PhotographerService photographerService;

    @GetMapping("/{photographerId}/session-types")
    public ApiResponse<String> getSessionTypes(@PathVariable("photographerId") Long photographerId) {
        photographerService.getSessionTypes(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{photographerId}")
    public ApiResponse<String> getPhotographer(@PathVariable("photographerId") Long photographerId) {
        photographerService.findById(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{photographerId}/additional-services")
    public ApiResponse<String> getAdditionalServices(@PathVariable("photographerId") Long photographerId) {
        photographerService.getAdditionalServices(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{photographerId}/work-hours")
    public ApiResponse<String> getWorkHours(@PathVariable("photographerId") Long photographerId) {
        photographerService.getWorkHours(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{photographerId}/portfolio")
    public ApiResponse<String> getPortfolio(@PathVariable("photographerId") Long photographerId) {
        // to do implement
        photographerService.getPortfolio(photographerId);
        // should I split for videos and photos ?
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{photographerId}/portfolio")
    public ApiResponse<String> addToPortfolio(@PathVariable("photographerId") Long photographerId) {
        // to do implement
        photographerService.updatePortfolio(photographerId);
        // should I split for videos and photos ?
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{photographerId}/session-types")
    public ApiResponse<String> setSessionTypes(@PathVariable("photographerId") Long photographerId) {
        photographerService.setSessionTypes(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{photographerId}/bookings")
    public ApiResponse<String> getBookings(@PathVariable("photographerId") Long photographerId) {
        photographerService.getBookings(photographerId);
        // to do get with from/to query params or status
        // pending/cancelled/booked/open/etc
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{photographerId}/reviews")
    public ApiResponse<String> getReviews(@PathVariable("photographerId") Long photographerId) {
        // to do implement
        photographerService.getReviews(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PutMapping("/{photographerId}/profile")
    public ApiResponse<String> updateProfile(@PathVariable("photographerId") Long photographerId) {
        // to do implement
        photographerService.updateProfile(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{photographerId}/membership")
    public ApiResponse<String> setMembership(@PathVariable("photographerId") Long photographerId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{photographerId}/work-hours")
    public ApiResponse<String> setWorkHours(@PathVariable("photographerId") Long photographerId) {
        // to do implement
        photographerService.setWorkHours(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{photographerId}/additional-services")
    public ApiResponse<String> setAdditionalServices(@PathVariable("photographerId") Long photographerId) {
        // to do implement
        photographerService.setAdditionalServices(photographerId);
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PatchMapping("/{photographerId}/bookings/{bookingId}/deposit")
    public ApiResponse<String> updateDepositStatus(@PathVariable("photographerId") Long photographerId,
            @PathVariable("bookingId") Long bookingId) {
        // to do implement
        // maybe not here
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

}
