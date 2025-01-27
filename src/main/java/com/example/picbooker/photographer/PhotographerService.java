package com.example.picbooker.photographer;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.picbooker.additionalService.AdditionalService;
import com.example.picbooker.additionalService.AdditionalServiceRepository;
import com.example.picbooker.workhours.WorkHour;
import com.example.picbooker.workhours.WorkHourService;

import jakarta.transaction.Transactional;

@Service
public class PhotographerService {

    @Autowired
    private PhotographerRepository photographerRepository;

    @Autowired
    private WorkHourService workHourService;

    @Autowired
    private AdditionalServiceRepository additionalServiceRepository;

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

    @Transactional
    public void setWorkHours(Long id, List<WorkHour> workhours) {

        workhours.stream().forEach(workhour -> {
            WorkHour workHour = workHourService.findForPhotographerAndDay(id, workhour.getDay());
            // to think , should I check more valid hours
            if (!isNull(workhour) && !isNull(workHour.getStartHour()) && !isNull(workhour.getEndHour())) {
                workHour.setStartHour(workhour.getStartHour());
                workHour.setEndHour(workhour.getEndHour());
            }
        });
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

    @Transactional
    public void updateProfile(Long photographerId, PhotographerDTO photographerDTO) {
        Photographer photographer = findByIdThrow(photographerId);
        if (!isNull(photographerDTO.getBufferTimeMinutes()))
            photographer.setBufferTimeMinutes(photographerDTO.getBufferTimeMinutes());
        if (!isNull(photographerDTO.getMinimumNoticeBeforeSessionMinutes()))
            photographer.setMinimumNoticeBeforeSessionMinutes(photographerDTO.getMinimumNoticeBeforeSessionMinutes());
        if (!isNull(photographerDTO.getStudio()))
            photographer.setStudio(photographerDTO.getStudio());
    }

    public void getSessionTypes(Long id) {
        // to do implement ;
    }

    public void setSessionTypes(Long id) {
        // to do implement ;
    }

    public void setAdditionalServices(Long photographerId, List<AdditionalService> additionalServices) {
        // to do implement ,
        // think of having one for edit , one for addition
        additionalServices.stream().forEach(additionalService -> {
            // additionalServiceRepository.save() ;
        });
    }

    public void getAdditionalServices(Long id) {
        // to do implement ;
    }

    // function to create custom private session and generate link to send to client

    // get photos from instagram integration
}