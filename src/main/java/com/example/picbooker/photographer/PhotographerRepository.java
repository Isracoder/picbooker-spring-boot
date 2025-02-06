package com.example.picbooker.photographer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.picbooker.user.User;

@Repository
public interface PhotographerRepository extends JpaRepository<Photographer, Long> {
    boolean existsByUser(User user);

    Optional<Photographer> findByUser(User user);
}