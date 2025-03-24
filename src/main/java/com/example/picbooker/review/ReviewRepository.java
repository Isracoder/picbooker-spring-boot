package com.example.picbooker.review;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // to change to pagination and pagedto with sorting
    Page<Review> findAllByPhotographer_Id(Long photographerId, Pageable pageable);

    Page<Review> findAllByClient_Id(Long clientId, Pageable pageable);

    Optional<Review> findAllByClient_IdAndPhotographer_Id(Long clientId, Long photographerId);
}