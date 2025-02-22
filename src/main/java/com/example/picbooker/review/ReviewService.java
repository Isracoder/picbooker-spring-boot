package com.example.picbooker.review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.client.Client;
import com.example.picbooker.photographer.Photographer;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Transactional
    public Review create(Photographer photographer, Client client, Integer rating, String description) {
        return new Review(null, photographer, client, rating, description, LocalDateTime.now());

    }

    @Transactional
    public Review createAndSave(Photographer photographer, Client client, Integer rating, String description) {
        return save(create(photographer, client, rating, description));
    }

    @Transactional(readOnly = true)
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public Review findByIdThrow(Long id) {
        return reviewRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<Review> findForPhotographer(Long photographerId) {
        return reviewRepository.findAllByPhotographer_Id(photographerId);
    }

    @Transactional(readOnly = true)
    public List<Review> findForClient(Long clientId) {
        return reviewRepository.findAllByClient_Id(clientId);
    }

    public Review writeReview(Client client, Photographer photographer, ReviewDTO reviewDTO) {

        return createAndSave(photographer, client, reviewDTO.getRating(), reviewDTO.getComment());
    }

}
