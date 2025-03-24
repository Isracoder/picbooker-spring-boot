package com.example.picbooker.review;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Review create(Photographer photographer, Client client, Double rating, String description) {
        return new Review(null, photographer, client, rating, description, LocalDateTime.now());

    }

    @Transactional
    public Review createAndSave(Photographer photographer, Client client, Double rating, String description) {
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
    public Page<Review> findForPhotographer(Long photographerId, Pageable pageable) {
        return reviewRepository.findAllByPhotographer_Id(photographerId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Review> findForClient(Long clientId, Pageable pageable) {
        return reviewRepository.findAllByClient_Id(clientId, pageable);
    }

    @Transactional
    public Review writeOrUpdateReview(Client client, Photographer photographer, ReviewDTO reviewDTO) {
        Optional<Review> previousReview = reviewRepository.findAllByClient_IdAndPhotographer_Id(client.getId(),
                photographer.getId());
        if (previousReview.isPresent()) {
            Review review = previousReview.get();
            review.setRating(reviewDTO.getRating());
            review.setComment(reviewDTO.getComment());
            review.setLeftAt(LocalDateTime.now());
            return save(review);

        }
        return createAndSave(photographer, client, reviewDTO.getRating(), reviewDTO.getComment());
    }

}
