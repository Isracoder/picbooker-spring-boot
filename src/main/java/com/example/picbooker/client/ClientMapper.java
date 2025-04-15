package com.example.picbooker.client;

import com.example.picbooker.user.UserMapper;

public class ClientMapper {

    public static ClientResponse toResponse(Client client) {
        if (client == null)
            return null;
        ClientResponse clientResponse = ClientResponse.builder()
                .userResponse(UserMapper.toResponse(client.getUser()))
                .pointsBalance(client.getPointsBalance())
                .personalName(client.getPersonalName())
                .id(client.getId())
                .build();
        return clientResponse;
    }

}
