package com.example.picbooker.review;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public void create() {
        // to do implement ;
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public Review findByIdThrow(Long id) {
        return reviewRepository.findById(id).orElseThrow();
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public void findForPhotographer() {
        // to do implement ;
    }

    public void findForClient() {
        // to do implement ;
    }

}
