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
                .DOB(userReq.getDOB())
                .password(userReq.getPassword())
                .city(userReq.getCity())
                .photoUrl(userReq.getPhotoURL())
                .country(userReq.getCountry())
                .build();
        return user;
    }

    public static User toEntity(UserResponse userResponse) {
        User user = User.builder().username(userResponse.getUsername())
                .email(userResponse.getEmail())
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
            finalUser.setPassword(userUpdates.getPassword()); // to do check encoding
        if (userUpdates.getDOB() != null)
            finalUser.setDOB(userUpdates.getDOB());
        if (userUpdates.getPhotoUrl() != null)
            finalUser.setPhotoUrl(userUpdates.getPhotoUrl());
        if (userUpdates.getCountry() != null)
            finalUser.setCountry(userUpdates.getCountry());
        if (userUpdates.getCity() != null)
            finalUser.setCity(userUpdates.getCity());
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
