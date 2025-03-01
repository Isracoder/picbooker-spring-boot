package com.example.picbooker.user;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
import com.example.picbooker.client.Client;
import com.example.picbooker.client.ClientService;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.photographer.PhotographerService;
import com.example.picbooker.security.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private ClientService clientService;

    @Autowired
    private PhotographerService photographerService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    // put token in cookie
    // {"Authorization" : "Bearer jwtoken" }
    // get : /auth/login :
    // function : from textbox -> object
    // in onClick : (data)
    // in js : fetch ("/localhost:880/auth.login").header("Authorization" : "Bearer
    // :
    // eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMjExMDc2MUBzdHVkZW50cy5oZWJyb24uZWR1IiwiaWF0IjoxNzM4MDc5NzQzLCJleHAiOjE3MzgyNTI1NDN9.VvS4cQBYuLhh1wqaYKsg6EftOEQpffh08xt3P02DpJU")
    // .method ("POST").requestBody(data).then (success -> redirect) .catch (alert
    // user) ;

    @GetMapping("/all")
    public ApiResponse<List<UserResponse>> findAllUsers(
            @PageableDefault(size = 10, direction = Sort.Direction.ASC, sort = "id") Pageable page) {

        Page<UserResponse> usersResPage = service.findAll(page);
        return ApiResponse.<List<UserResponse>>builder()
                .content(usersResPage.getContent())
                .status(HttpStatus.OK)
                .build();

    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> findById(@PathVariable("id") long id) {

        return ApiResponse.<UserResponse>builder()
                .content(service.findUserResponseById(id))
                .status(HttpStatus.OK)
                .build();

    }

    @DeleteMapping("/{id}") // to do secure only for admin
    public ApiResponse<String> deleteById(@PathVariable("id") long id) {

        service.delete(id);
        return ApiResponse.<String>builder()
                .content("Successful deletion")
                .status(HttpStatus.OK)
                .build();

    }

    @PostMapping("/")
    public ApiResponse<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse userRes = service.createAndSave(userRequest);
        return ApiResponse.<UserResponse>builder()
                .content(userRes)
                .status(HttpStatus.OK)
                .build();

    }

    // think of having for admin
    // @PutMapping("/{id}")
    // public ApiResponse<UserResponse> updateUserById(@PathVariable("id") long id,
    // @RequestBody UserRequest userRequest) {

    // UserResponse userRes = service.updateAccountById(id, userRequest);
    // return ApiResponse.<UserResponse>builder()
    // .content(userRes)
    // .status(HttpStatus.OK)
    // .build();

    // }

    @PatchMapping("/my-account")
    public ApiResponse<UserResponse> updateUserAccountDetails(
            @RequestBody UserRequest userRequest) {

        User user = UserService.getLoggedInUserThrow();

        UserResponse userResponse = service.updateAccount(
                user,
                userRequest);
        return ApiResponse.<UserResponse>builder()
                .content(userResponse)
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {

            service.changePassword(passwordChangeDTO, UserService.getLoggedInUserThrow());
            return ApiResponse.<String>builder().status(HttpStatus.OK).content("Password changed successfully.")
                    .build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> info() {
        // Retrieve the currently authenticated user's details
        User user = UserService.getLoggedInUserThrow();
        Optional<Photographer> opt1 = photographerService.getPhotographerFromUser(user);
        Optional<Client> opt2 = clientService.getClientFromUser(user);
        UserResponse response = UserMapper.toResponse(user);
        if (opt1.isPresent())
            response.setPhotographerId(opt1.get().getId());
        if (opt2.isPresent())
            response.setClientId(opt2.get().getId());
        return ApiResponse.<UserResponse>builder()
                .content(response)
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if (!isNull(token)) {
            token.replace("Bearer ", "");
            tokenBlacklistService.blacklistToken(token);
        }
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // Only in production with HTTPS
                .path("/")
                .maxAge(0) // Expire immediately
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();

    }

    @PostMapping("/{id}/subscriptions/pro")
    public ApiResponse<String> subscribeToPro(@PathVariable("id") long id) {
        try {
            // to do implement
            // send userId in body maybe , with type
            // maybe payment info
            return ApiResponse.<String>builder()
                    .content("not implemented")
                    .status(HttpStatus.NOT_IMPLEMENTED)
                    .build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .content(e.getMessage())
                    .status(e.getStatus())
                    .build();
        }
    }

    @DeleteMapping("/{id}/subscriptions/pro")
    public ApiResponse<String> cancelSubscription() {
        try {
            // to do implement
            // send userId in body maybe , with type
            // maybe payment info
            return ApiResponse.<String>builder()
                    .content("not implemented")
                    .status(HttpStatus.NOT_IMPLEMENTED)
                    .build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .content(e.getMessage())
                    .status(e.getStatus())
                    .build();
        }
    }

}
