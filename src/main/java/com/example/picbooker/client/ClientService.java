package com.example.picbooker.client;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.review.Review;
import com.example.picbooker.session.Session;
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
        return new Client(null, user, 0, null, null, null);
    }

    public Client getClientFromUserThrow(User user) {
        Optional<Client> client = clientRepository.findByUser(user);
        if (!client.isPresent())
            throw new ApiException(HttpStatus.NOT_FOUND, "Client not found");
        return client.get();
    }

    public Optional<Client> getClientFromUser(User user) {
        Optional<Client> client = clientRepository.findByUser(user);
        return client;
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
    public ClientResponse assignClientRoleAndCreate(Long userId) {
        User user = userService.findByIdThrow(userId);
        // Optionally, check if the user already has an associated role record
        if (clientRepository.existsByUser(user)) {
            throw new ApiException(HttpStatus.CONFLICT, "User already has a client role");
        }
        Client client = save(create(user));
        user.setClient(client);

        return ClientMapper.toResponse(client);
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