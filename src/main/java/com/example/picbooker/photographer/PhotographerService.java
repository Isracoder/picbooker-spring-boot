package com.example.picbooker.photographer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotographerService {

    @Autowired
    private PhotographerRepository photographerRepository;

    public void create() {
        // to do implement ;
    }

    public Optional<Photographer> findById(Long id) {
        return photographerRepository.findById(id);
    }

    public Photographer findByIdThrow(Long id) {
        return photographerRepository.findById(id).orElseThrow();
    }

    public Photographer save(Photographer photographer) {
        return photographerRepository.save(photographer);
    }

    public void getWorkHours(Long id) {
        // to do implement ;
    }

    public void setWorkHours(Long id) {
        // to do implement ;
        // update for a specific day ?
    }

    public void getPortfolio(Long id) {
        // to do implement ;
    }

    public void updatePortfolio(Long id) {
        // to do implement ;
        // photos / videos ?
    }

    public void getBookings(Long photographerId) {
        // to do implement ;
        // maybe not here ? or call booking service ;
    }

    public void getReviews(Long photographerId) {
        // to do implement ;
        // maybe not here ? or call review service ;
    }

    public void updateProfile(Long photographerId) {
        // to do implement ;
        // maybe not here ?
    }

    public void getSessionTypes(Long id) {
        // to do implement ;
    }

    public void setSessionTypes(Long id) {
        // to do implement ;
    }

    public void setAdditionalServices(Long id) {
        // to do implement ;
    }

    public void getAdditionalServices(Long id) {
        // to do implement ;
    }

    // function to create custom private session and generate link to send to client

    // get photos from instagram integration
}