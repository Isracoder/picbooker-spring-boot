package com.example.picbooker.photographer;

import com.example.picbooker.user.User;
import com.example.picbooker.user.UserMapper;

public class PhotographerMapper {

    public static Photographer addToEntity(Photographer photographer, PhotographerRequest photographerReq) {
        photographer.setStudio(photographerReq.getStudio());
        photographer.setBufferTimeMinutes(photographerReq.getBufferTimeMinutes());
        photographer.setMinimumNoticeBeforeSessionMinutes(photographerReq.getMinimumNoticeBeforeSessionMinutes());
        photographer.setWorkhours(photographerReq.getWorkhours());

        return photographer;
    }

    public static Photographer toEntityFromRequest(PhotographerRequest photographerRequest, User user) {
        Photographer photographer = Photographer.builder()
                .user(user)
                .bufferTimeMinutes(photographerRequest.getBufferTimeMinutes())
                .minimumNoticeBeforeSessionMinutes(photographerRequest.getMinimumNoticeBeforeSessionMinutes())
                .studio(photographerRequest.getStudio())
                .bio(photographerRequest.getBio())
                .workhours(photographerRequest.getWorkhours())
                .build();
        return photographer;
    }

    public static PhotographerResponse toResponse(Photographer photographer) {
        PhotographerResponse photographerResponse = PhotographerResponse.builder()
                .userResponse(UserMapper.toResponse(photographer.getUser()))
                .studio(photographer.getStudio())
                .minimumNoticeBeforeSessionMinutes(photographer.getMinimumNoticeBeforeSessionMinutes())
                .bufferTimeMinutes(photographer.getBufferTimeMinutes())
                .bio(photographer.getBio())
                .workhours(photographer.getWorkhours())
                .id(photographer.getId())
                .build();
        return photographerResponse;
    }

}
