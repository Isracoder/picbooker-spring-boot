package com.example.picbooker.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/{clientId}")
    public ApiResponse<String> getClient(@PathVariable("clientId") Integer clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{clientId}/favorite")
    public ApiResponse<String> getClientFavoritePhotographers(@PathVariable("clientId") Integer clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{clientId}/favorite")
    public ApiResponse<String> setClientFavoritePhotographers(@PathVariable("clientId") Integer clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{clientId}/bookings")
    public ApiResponse<String> getBookings(@PathVariable("clientId") Integer clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping("/{clientId}/reviews")
    public ApiResponse<String> getReviews(@PathVariable("clientId") Integer clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PutMapping("/{clientId}/profile")
    public ApiResponse<String> updateProfile(@PathVariable("clientId") Integer clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{clientId}/membership")
    public ApiResponse<String> setMembership(@PathVariable("clientId") Integer clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

}
