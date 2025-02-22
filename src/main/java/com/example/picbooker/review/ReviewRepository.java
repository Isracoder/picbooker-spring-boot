package com.example.picbooker.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // to change to pagination and pagedto with sorting
    List<Review> findAllByPhotographer_Id(Long photographerId);

    List<Review> findAllByClient_Id(Long clientId);
}