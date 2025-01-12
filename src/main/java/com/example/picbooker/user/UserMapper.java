package com.example.picbooker.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class UserMapper {

    public static User toEntity(UserRequest userReq) {
        User user = User.builder().username(userReq.getUsername())
                .email(userReq.getEmail())
                // .phoneNumber(userReq.getPhoneNumber())
                .password(userReq.getPassword())
                // .role(userReq.getRole())
                .build();
        return user;
    }

    public static User toEntity(UserResponse userReq) {
        User user = User.builder().username(userReq.getUsername())
                .email(userReq.getEmail())
                // .role(userReq.getRole())
                .build();
        return user;
    }

    public static UserResponse toResponse(User user) {
        UserResponse userResponse = UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .id(user.getId())
                .build();
        return userResponse;
    }

    public static User merge(User userUpdates, User finalUser) {
        if (userUpdates.getUsername() != null)
            finalUser.setUsername(userUpdates.getUsername());
        if (userUpdates.getEmail() != null)
            finalUser.setEmail(userUpdates.getEmail());
        if (userUpdates.getPassword() != null)
            finalUser.setPassword(userUpdates.getPassword());
        return finalUser;

    }

    public static ArrayList<UserResponse> toResponse(List<User> users) {
        return users.stream().map(user -> toResponse(user))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Page<UserResponse> toResponsePage(Page<User> users) {
        List<UserResponse> userResponses = users.getContent().stream()
                .map(user -> toResponse(user))
                .collect(Collectors.toList());
        return new PageImpl<>(userResponses, users.getPageable(), users.getTotalElements());

    }

}
