package com.example.picbooker.photographer;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.picbooker.ApiException;
import com.example.picbooker.additionalService.AdditionalService;
import com.example.picbooker.additionalService.AdditionalServiceRepository;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;
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
    private UserService userService;

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

    @Transactional
    public PhotographerResponse assignPhotographerRoleAndCreate(Long userId, PhotographerRequest photographerRequest) {
        User user = userService.findByIdThrow(userId);
        // Optionally, check if the user already has an associated role record
        if (photographerRepository.existsByUser(user)) {
            throw new ApiException(HttpStatus.CONFLICT, "User already has a photographer role");
        }
        Photographer photographer = save(PhotographerMapper.toEntityFromRequest(photographerRequest, user));
        user.setPhotographer(photographer);

        return PhotographerMapper.toResponse(photographer);

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
    public PhotographerResponse updateProfile(Long photographerId, PhotographerRequest photographerRequest) {
        Photographer photographer = findByIdThrow(photographerId);
        if (!isNull(photographerRequest.getBufferTimeMinutes()))
            photographer.setBufferTimeMinutes(photographerRequest.getBufferTimeMinutes());
        if (!isNull(photographerRequest.getMinimumNoticeBeforeSessionMinutes()))
            photographer
                    .setMinimumNoticeBeforeSessionMinutes(photographerRequest.getMinimumNoticeBeforeSessionMinutes());
        if (!isNull(photographerRequest.getStudio()))
            photographer.setStudio(photographerRequest.getStudio());
        if (!isNull(photographerRequest.getBio()))
            photographer.setBio(photographerRequest.getBio());
        return PhotographerMapper.toResponse(photographer);

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