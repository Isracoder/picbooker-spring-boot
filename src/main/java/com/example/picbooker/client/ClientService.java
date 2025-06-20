package com.example.picbooker.client;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.photographer.PhotographerMapper;
import com.example.picbooker.photographer.PhotographerResponse;
import com.example.picbooker.photographer.PhotographerService;
import com.example.picbooker.review.Review;
import com.example.picbooker.review.ReviewDTO;
import com.example.picbooker.review.ReviewService;
import com.example.picbooker.session.SessionRepository;
import com.example.picbooker.session.SessionStatus;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PhotographerService photographerService;

    @Autowired
    private SessionRepository sessionRepository;

    public Client create(User user) {
        return new Client(null, user, 0, null, null, null, null);
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
        userService.save(user);

        return ClientMapper.toResponse(client);
    }

    public Set<PhotographerResponse> getClientFavoritePhotographers(Long clientId) {
        // to do change to mapper ;

        Client client = findByIdThrow(clientId);
        return PhotographerMapper.toResponseSet(client.getFavoritePhotographers());
    }

    @Transactional
    public void addToClientFavoritePhotographers(Client client, Long photographerId) {

        Photographer photographer = photographerService.findByIdThrow(photographerId);
        client.getFavoritePhotographers().add(photographer);
    }

    @Transactional
    public void removeFromClientFavoritePhotographers(Client client, Long photographerId) {
        Photographer photographer = photographerService.findByIdThrow(photographerId);
        client.getFavoritePhotographers().remove(photographer);
    }

    public List<Review> getReviews(Client client) {

        return client.getReviews();
    }

    public Page<Review> getReviews(Long clientId, Pageable pageable) {
        return reviewService.findForClient(clientId, pageable);
    }

    public Review writeOrUpdateReview(Client client, ReviewDTO reviewDTO) {
        Photographer photographer = photographerService.findByIdThrow(reviewDTO.getPhotographerId());
        if (!sessionRepository.existsByStatusAndClient_IdAndPhotographer_IdAndDateBefore(SessionStatus.BOOKED,
                client.getId(), photographer.getId(), LocalDate.now())) {

            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Can't write a review for a photographer that you haven't had a session with");
        }
        return reviewService.writeOrUpdateReview(client, photographer, reviewDTO);
    }

    @Transactional
    public ClientResponse updateProfile(Client client, ClientResponse clientRequest) {
        client = findByIdThrow(client.getId());
        if (!isNull(clientRequest.getPersonalName()))
            client.setPersonalName(clientRequest.getPersonalName());
        return ClientMapper.toResponse(client);

    }
}