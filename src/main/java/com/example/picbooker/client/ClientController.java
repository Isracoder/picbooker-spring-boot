package com.example.picbooker.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.review.Review;
import com.example.picbooker.session.Session;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/{clientId}")
    public ApiResponse<Client> getClient(@PathVariable("clientId") Long clientId) {

        return ApiResponse.<Client>builder()
                .content(clientService.findByIdThrow(clientId))
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<ClientResponse> info() {
        // Retrieve the currently authenticated user's details
        ClientResponse response = ClientMapper
                .toResponse(clientService.getClientFromUserThrow(UserService.getLoggedInUserThrow()));
        return ApiResponse.<ClientResponse>builder()
                .content(response)
                .status(HttpStatus.OK)
                .build();
    }

    // seting yourself as client
    @PostMapping("/")
    public ApiResponse<ClientResponse> openClientAccount() {
        User user = UserService.getLoggedInUserThrow();
        ClientResponse client = clientService.assignClientRoleAndCreate(user.getId());
        return ApiResponse.<ClientResponse>builder()
                .content(client)
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{clientId}/favorite")
    public ApiResponse<List<Photographer>> getClientFavoritePhotographers(@PathVariable("clientId") Long clientId) {

        return ApiResponse.<List<Photographer>>builder()
                .content(clientService.getClientFavoritePhotographers(clientId))
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/{clientId}/favorite")
    public ApiResponse<String> setClientFavoritePhotographers(@PathVariable("clientId") Long clientId,
            @RequestBody Long photographerId) {
        clientService.addToClientFavoritePhotographers(clientId, photographerId);
        return ApiResponse.<String>builder()
                .content("Succesfully added photographer " + photographerId + " as a favorite")
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{clientId}/bookings")
    public ApiResponse<List<Session>> getBookings(@PathVariable("clientId") Long clientId) {
        return ApiResponse.<List<Session>>builder()
                .content(clientService.getBookings(clientId))
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{clientId}/reviews")
    public ApiResponse<List<Review>> getReviews(@PathVariable("clientId") Long clientId) {

        return ApiResponse.<List<Review>>builder()
                .content(clientService.getReviews(clientId))
                .status(HttpStatus.OK)
                .build();
    }

    @PutMapping("/{clientId}/profile")
    public ApiResponse<String> updateProfile(@PathVariable("clientId") Long clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping("/{clientId}/membership")
    public ApiResponse<String> setMembership(@PathVariable("clientId") Long clientId) {
        // to do implement
        return ApiResponse.<String>builder()
                .content("Not implemented")
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

}
