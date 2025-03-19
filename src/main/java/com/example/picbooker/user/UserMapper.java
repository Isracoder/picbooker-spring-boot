package com.example.picbooker.user;

import static java.util.Objects.isNull;

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
                .phoneNumber(userReq.getPhoneNumber())
                .password(userReq.getPassword())
                .gender(userReq.getGender())
                .city(userReq.getCity())
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
                .phoneNumber(user.getPhoneNumber())
                .isEmailVerified(user.getIsEmailVerified())
                .email(user.getEmail())
                .gender(user.getGender())
                .country(user.getCountry())
                .city(user.getCity())
                .gender(user.getGender())
                .DOB(user.getDOB())
                .id(user.getId())
                .build();
        return userResponse;
    }

    public static User merge(UserRequest userUpdates, User finalUser) {
        // if (userUpdates.getUsername() != null)
        // finalUser.setUsername(userUpdates.getUsername()); // for later check jwt no
        // break
        if (!isNull(userUpdates.getPhoneNumber()))
            finalUser.setPhoneNumber(userUpdates.getPhoneNumber());
        if (userUpdates.getDOB() != null)
            finalUser.setDOB(userUpdates.getDOB());
        if (userUpdates.getCountry() != null)
            finalUser.setCountry(userUpdates.getCountry());
        if (userUpdates.getCity() != null)
            finalUser.setCity(userUpdates.getCity());
        if (!isNull(userUpdates.getGender()))
            finalUser.setGender(userUpdates.getGender());
        System.out.println("Account merged successfully for user " + finalUser.getId());
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

    // public static <T> PageDTO<T> toGeneralResponsePage(Page<T> games) {
    // List<T> gameResponses = games.getContent().stream()
    // .collect(Collectors.toList());
    // return new PageDTO<>(gameResponses, games.getTotalPages(),
    // games.getTotalElements(), games.getNumber());

    // }

}
