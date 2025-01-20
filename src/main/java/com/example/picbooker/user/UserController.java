package com.example.picbooker.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiException;
import com.example.picbooker.ApiResponse;
import com.example.picbooker.security.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @GetMapping("/all")
    public ApiResponse<?> findAllUsers(
            @PageableDefault(size = 10, direction = Sort.Direction.ASC, sort = "id") Pageable page) {
        try {
            Page<UserResponse> usersResPage = service.findAll(page);
            return ApiResponse.<List<UserResponse>>builder()
                    .content(usersResPage.getContent())
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content("Something went wrong :(")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<?> findById(@PathVariable("id") long id) {
        try {
            return ApiResponse.<UserResponse>builder()
                    .content(service.findUserResponseById(id))
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content("Something went wrong :(")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @DeleteMapping("/{id}") // to do secure only for admin
    public ApiResponse<?> deleteById(@PathVariable("id") long id) {
        try {

            service.delete(id);
            return ApiResponse.<String>builder()
                    .content("Successful deletion")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content("Something went wrong :(")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/")
    public ApiResponse<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse userRes = service.save(userRequest);
        return ApiResponse.<UserResponse>builder()
                .content(userRes)
                .status(HttpStatus.OK)
                .build();

    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateUserById(@PathVariable("id") long id, @RequestBody UserRequest userRequest) {
        try {
            UserResponse userRes = service.update(userRequest, id);
            return ApiResponse.<UserResponse>builder()
                    .content(userRes)
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content(e.getLocalizedMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            service.changePassword(passwordChangeDTO);
            return ApiResponse.builder().status(HttpStatus.OK).content("Password changed successfully.").build();
        } catch (ApiException e) {
            return ApiResponse.builder()
                    .status(e.getStatus())
                    .content(e.getMessage())
                    .build();
        }

    }

    @GetMapping("/info")
    public ApiResponse<?> info() {
        // Retrieve the currently authenticated user's details
        User player = UserService.getLoggedInUser();

        return ApiResponse.<Object>builder()
                .content(UserMapper.toResponse(player))
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        try {

            String token = request.getHeader("Authorization").replace("Bearer ", "");
            tokenBlacklistService.blacklistToken(token);

            return ApiResponse.<String>builder()
                    .content("Logout Successful")
                    .status(HttpStatus.OK)
                    .build();
        } catch (ApiException e) {
            return ApiResponse.<String>builder()
                    .content(e.getMessage())
                    .status(e.getStatus())
                    .build();
        }
    }

    @PostMapping("/{id}/subscriptions/pro")
    public ApiResponse<String> subscribeToPro() {
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
