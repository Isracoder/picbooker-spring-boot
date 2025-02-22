package com.example.picbooker.photographer;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiException;
import com.example.picbooker.ApiResponse;
import com.example.picbooker.photographer_additionalService.PhotographerAddOn;
import com.example.picbooker.photographer_additionalService.PhotographerAddOnDTO;
import com.example.picbooker.photographer_sessionType.PhotographerSessionType;
import com.example.picbooker.photographer_sessionType.PhotographerSessionTypeDTO;
import com.example.picbooker.photographer_sessionType.PhotographerSessionTypeService;
import com.example.picbooker.review.Review;
import com.example.picbooker.security.JwtUtil;
import com.example.picbooker.socialLinks.SocialLink;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;
import com.example.picbooker.workhours.WorkHourDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/photographers")
public class PhotographerController {

        @Autowired
        private PhotographerService photographerService;

        @Autowired
        private PhotographerSessionTypeService photographerSessionTypeService;

        @Autowired
        private JwtUtil jwtUtil;

        @GetMapping("/me")
        public ApiResponse<PhotographerResponse> info() {
                // Retrieve the currently authenticated user's details
                PhotographerResponse response = PhotographerMapper.toResponse(
                                photographerService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow()));
                return ApiResponse.<PhotographerResponse>builder()
                                .content(response)
                                .status(HttpStatus.OK)
                                .build();
        }

        // seting yourself as client
        @PostMapping("/")
        public ApiResponse<PhotographerResponse> openPhotographerAccount(
                        @Valid @RequestBody PhotographerRequest photographerRequest,
                        @CookieValue(name = "jwt", required = false) String token) {

                Long loggedInUserId = null;
                System.out.println("Is cookie token null ? " + isNull(token));
                if (token != null) {
                        loggedInUserId = jwtUtil.getUserId(token);
                }

                // If JWT is present, use the ID from the token (more secure)
                if (loggedInUserId != null) {
                        System.out.println("Logged in id: " + loggedInUserId + " , sent user Id: "
                                        + photographerRequest.getUserId());
                        if (!loggedInUserId.equals(photographerRequest.getUserId())) {
                                throw new ApiException(HttpStatus.UNAUTHORIZED,
                                                "You are not authorized to perform this action.");
                        }
                        photographerRequest.setUserId(loggedInUserId);
                }
                PhotographerResponse photographer = photographerService.assignPhotographerRoleAndCreate(
                                photographerRequest.getUserId(),
                                photographerRequest);
                return ApiResponse.<PhotographerResponse>builder()
                                .content(photographer)
                                .status(HttpStatus.OK)
                                .build();
        }

        // GET /photographers/{id}/session-types → Get all session types for a
        // photographer
        // POST /photographers/{id}/session-types → Add a session type to a photographer
        // DELETE /photographers/{id}/session-types/{sessionTypeId} → Remove a session
        // type
        // PUT /photographers/{id}/session-types/{sessionTypeId}

        @PostMapping("/session-types")
        public ApiResponse<PhotographerSessionType> setSessionTypes(
                        @RequestBody PhotographerSessionTypeDTO photographerSessionTypeDTO) {
                PhotographerSessionType photoSessionType = photographerSessionTypeService.addSessionType(
                                photographerService.findByIdThrow(UserService.getLoggedInUserThrow().getId()),
                                photographerSessionTypeDTO);
                return ApiResponse.<PhotographerSessionType>builder()
                                .content(photoSessionType)
                                .status(HttpStatus.OK)
                                .build();
        }

        @PatchMapping("/session-types/{sessionTypeId}")
        public ApiResponse<PhotographerSessionType> updateSessionType(@PathVariable("sessionTypeId") Long sessionTypeId,
                        @RequestBody PhotographerSessionTypeDTO photographerSessionTypeDTO) {
                PhotographerSessionType photoSessionType = photographerService.updateSessionType(
                                photographerService.findByIdThrow(UserService.getLoggedInUserThrow().getId()),
                                sessionTypeId,
                                photographerSessionTypeDTO);
                return ApiResponse.<PhotographerSessionType>builder()
                                .content(photoSessionType)
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{photographerId}/session-types")
        public ApiResponse<List<PhotographerSessionType>> getSessionTypes(
                        @PathVariable("photographerId") Long photographerId) {
                // TODO: add ability to get specific session type
                return ApiResponse.<List<PhotographerSessionType>>builder()
                                .content(photographerService.getSessionTypes(photographerId))
                                .status(HttpStatus.OK)
                                .build();
        }

        @DeleteMapping("/session-types/{sessionTypeId}")
        public ApiResponse<String> deleteByTypeId(@PathVariable("sessionTypeId") Long sessionTypeId) {
                photographerSessionTypeService.deleteById(
                                photographerService.findByIdThrow(UserService.getLoggedInUserThrow().getId()),
                                sessionTypeId);
                return ApiResponse.<String>builder()
                                .content("Successfully deleted")
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{photographerId}")
        public ApiResponse<PhotographerResponse> getPhotographer(@PathVariable("photographerId") Long photographerId) {
                Photographer photographer = photographerService.findByIdThrow(photographerId);
                return ApiResponse.<PhotographerResponse>builder()
                                .content(PhotographerMapper.toResponse(photographer))
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{photographerId}/add-ons")
        public ApiResponse<List<PhotographerAddOn>> getAddOns(@PathVariable("photographerId") Long photographerId) {

                return ApiResponse.<List<PhotographerAddOn>>builder()
                                .content(photographerService.getAddOnsForPhotographer(photographerId))
                                .status(HttpStatus.OK)
                                .build();
        }

        @PatchMapping("/add-ons/{add-onId}")
        public ApiResponse<PhotographerAddOn> updateAddOn(@PathVariable("add-onId") Long addOnId,
                        @RequestBody PhotographerAddOnDTO request) {
                PhotographerAddOn addOn = photographerService.updateAddOn(
                                photographerService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow()),
                                addOnId, request);
                return ApiResponse.<PhotographerAddOn>builder()
                                .content(addOn)
                                .status(HttpStatus.OK)
                                .build();
        }

        @PostMapping("/add-ons")
        public ApiResponse<PhotographerAddOn> createAdditionalService(
                        @RequestBody PhotographerAddOnDTO photographerAddOnDTO) {
                PhotographerAddOn addOn = photographerService.createAddOn(
                                photographerService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow()),
                                photographerAddOnDTO);
                return ApiResponse.<PhotographerAddOn>builder()
                                .content(addOn)
                                .status(HttpStatus.OK)
                                .build();
        }

        @DeleteMapping("/add-ons/{add-onId}")
        public ApiResponse<String> deleteAdditionalServiceByName(@PathVariable("add-onId") Long addOnId) {
                photographerService.deleteAddOnById(
                                photographerService.findByIdThrow(UserService.getLoggedInUserThrow().getId()),
                                addOnId);
                return ApiResponse.<String>builder()
                                .content("Successfully deleted")
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{photographerId}/work-hours")
        public ApiResponse<List<WorkHourDTO>> getWorkHours(@PathVariable("photographerId") Long photographerId) {
                List<WorkHourDTO> workhours = photographerService.getWorkHours(photographerId);
                return ApiResponse.<List<WorkHourDTO>>builder()
                                .content(workhours)
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{photographerId}/socials")
        public ApiResponse<List<SocialLink>> getSocials(@PathVariable("photographerId") Long photographerId) {
                List<SocialLink> socials = photographerService.getSocials(photographerId);
                return ApiResponse.<List<SocialLink>>builder()
                                .content(socials)
                                .status(HttpStatus.OK)
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
        public ApiResponse<List<Review>> getReviews(@PathVariable("photographerId") Long photographerId) {
                // to do implement
                List<Review> reviews = photographerService.getReviews(photographerId);
                return ApiResponse.<List<Review>>builder()
                                .content(reviews)
                                .status(HttpStatus.OK)
                                .build();
        }

        // to do rename to me and get from token
        @PatchMapping("/profile")
        public ApiResponse<PhotographerResponse> updateProfile(
                        @RequestBody PhotographerRequest photographerRequest) {

                User user = UserService.getLoggedInUserThrow();

                PhotographerResponse photographerResponse = photographerService.updateProfile(
                                photographerService.getPhotographerFromUserThrow(user),
                                photographerRequest);
                return ApiResponse.<PhotographerResponse>builder()
                                .content(photographerResponse)
                                .status(HttpStatus.OK)
                                .build();
        }

        @PatchMapping("/profile/socials")
        public ApiResponse<List<SocialLink>> updateSocialLinks(
                        @RequestBody ArrayList<SocialLink> socialLinks) {

                User user = UserService.getLoggedInUserThrow();
                List<SocialLink> socials = photographerService.updateSocialLinks(
                                photographerService.getPhotographerFromUserThrow(user),
                                socialLinks);
                return ApiResponse.<List<SocialLink>>builder()
                                .content(socials)
                                .status(HttpStatus.OK)
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

        // to test
        @PatchMapping("/work-hours")
        public ApiResponse<List<WorkHourDTO>> setWorkHours(
                        @Valid @RequestBody List<WorkHourDTO> workhours) {
                // send data in format : [ {day: "MONDAY" , startHour: 8 , endHour: 15 } , {day:
                // "TUESDAY" , startHour : 8 , endHour: 15}]

                List<WorkHourDTO> updatedWorkHours = photographerService.setWorkHours(
                                photographerService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow()),
                                workhours);
                return ApiResponse.<List<WorkHourDTO>>builder()
                                .content(updatedWorkHours)
                                .status(HttpStatus.OK)
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
