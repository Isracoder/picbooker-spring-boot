package com.example.picbooker.client;

import static java.util.Objects.isNull;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiException;
import com.example.picbooker.ApiResponse;
import com.example.picbooker.PageDTO;
import com.example.picbooker.photographer.PhotographerResponse;
import com.example.picbooker.review.Review;
import com.example.picbooker.review.ReviewDTO;
import com.example.picbooker.security.JwtUtil;
import com.example.picbooker.session.SessionResponse;
import com.example.picbooker.session.SessionService;
import com.example.picbooker.session.SessionStatus;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

        @Autowired
        private ClientService clientService;

        @Autowired
        private SessionService sessionService;

        @Autowired
        private JwtUtil jwtUtil;

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
        public ApiResponse<ClientResponse> openClientAccount(@RequestBody Long userId,
                        @CookieValue(name = "jwt", required = false) String token) {

                Long loggedInUserId = null;
                System.out.println("Is cookie token null ? " + isNull(token));
                if (token != null) {
                        loggedInUserId = jwtUtil.getUserId(token);
                }

                // If JWT is present, use the ID from the token (more secure)
                if (loggedInUserId != null) {
                        System.out.println("Logged in id: " + loggedInUserId + " , sent user Id: " + userId);
                        if (!loggedInUserId.equals(userId)) {
                                throw new ApiException(HttpStatus.UNAUTHORIZED,
                                                "You are not authorized to perform this action.");
                        }
                        userId = loggedInUserId;
                }
                ClientResponse client = clientService.assignClientRoleAndCreate(userId);
                return ApiResponse.<ClientResponse>builder()
                                .content(client)
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{clientId}/favorites")
        public ApiResponse<Set<PhotographerResponse>> getClientFavoritePhotographers(
                        @PathVariable("clientId") Long clientId) {
                // to think of mapping to photographer favorite response
                // minimal photographer information
                return ApiResponse.<Set<PhotographerResponse>>builder()
                                .content(clientService.getClientFavoritePhotographers(clientId))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PostMapping("/favorites")
        public ApiResponse<Map<String, String>> setClientFavoritePhotographers(
                        @RequestBody Long photographerId) {
                Client client = clientService.getClientFromUserThrow(UserService.getLoggedInUserThrow());
                clientService.addToClientFavoritePhotographers(client, photographerId);
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        @DeleteMapping("/favorites")
        public ApiResponse<Map<String, String>> removeFromClientFavoritePhotographers(
                        @RequestBody Long photographerId) {
                Client client = clientService.getClientFromUserThrow(UserService.getLoggedInUserThrow());
                clientService.removeFromClientFavoritePhotographers(client, photographerId);
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/sessions")
        public ApiResponse<PageDTO<SessionResponse>> getBookings(
                        @PageableDefault Pageable pageable,
                        @RequestParam(name = "past", defaultValue = "false") Boolean past,
                        @RequestParam(name = "status", required = false) SessionStatus status) {
                PageDTO<SessionResponse> responses;
                Client client = UserService.getClientFromUserThrow(UserService.getLoggedInUserThrow());
                if (past) {

                        responses = sessionService.getPastForClient(client.getId(), pageable);
                } else {
                        responses = sessionService.getUpcomingSessionsForClientWhereStatusAndAfter(client.getId(),
                                        status,
                                        null, pageable);
                }
                return ApiResponse.<PageDTO<SessionResponse>>builder()
                                .content(responses)
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{clientId}/reviews")
        public ApiResponse<Page<Review>> getReviews(@PathVariable("clientId") Long clientId,
                        @PageableDefault Pageable pageable) {

                return ApiResponse.<Page<Review>>builder()
                                .content(clientService.getReviews(clientId, pageable))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PostMapping("/reviews")
        public ApiResponse<Review> addReview(@RequestBody ReviewDTO reviewDTO) {
                Client client = clientService.getClientFromUserThrow(UserService.getLoggedInUserThrow());
                return ApiResponse.<Review>builder()
                                .content(clientService.writeOrUpdateReview(client, reviewDTO))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PutMapping("/profile")
        public ApiResponse<ClientResponse> updateProfile(@RequestBody ClientResponse clientRequest) {
                User user = UserService.getLoggedInUserThrow();

                ClientResponse clientResponse = clientService.updateProfile(UserService.getClientFromUserThrow(user),
                                clientRequest);
                return ApiResponse.<ClientResponse>builder()
                                .content(clientResponse)
                                .status(HttpStatus.OK)
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
