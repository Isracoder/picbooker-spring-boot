package com.example.picbooker.client;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.review.Review;
import com.example.picbooker.session.Session;
import com.example.picbooker.user.RoleType;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

import jakarta.transaction.Transactional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserService userService;

    public Client create(User user) {
        return Client.builder().id(user.getId()).build();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Client findByIdThrow(Long id) {
        return clientRepository.findById(id).orElseThrow();
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Transactional
    public Client assignClientRoleAndCreate(Long userId) {
        User user = userService.findById(userId);
        user.setRole(RoleType.CLIENT);
        return save(create(user));

    }

    public List<Photographer> getClientFavoritePhotographers(Long clientId) {
        // to do implement ;
        return null;
    }

    public void addToClientFavoritePhotographers(Long clientId, Long photograherId) {
        // return null ;
        // to do implement ;
    }

    public List<Session> getBookings(Long clientId) {
        return null;
        // to do implement ;
    }

    public List<Review> getReviews(Long clientId) {
        return null;
        // to do implement ;
    }
}