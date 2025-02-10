package com.example.picbooker.photographer;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.picbooker.ApiException;
import com.example.picbooker.additionalService.AdditionalService;
import com.example.picbooker.additionalService.AdditionalServiceRepository;
import com.example.picbooker.socialLinks.SocialLink;
import com.example.picbooker.socialLinks.SocialLinkService;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;
import com.example.picbooker.workhours.WorkHour;
import com.example.picbooker.workhours.WorkHourDTO;
import com.example.picbooker.workhours.WorkHourService;

import jakarta.transaction.Transactional;

@Service
public class PhotographerService {

    @Autowired
    private PhotographerRepository photographerRepository;

    @Autowired
    private WorkHourService workHourService;

    @Autowired
    private SocialLinkService socialLinkService;

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

    public List<WorkHourDTO> getWorkHours(Long id) {
        Photographer photographer = findByIdThrow(id);
        if (isNull(photographer.getWorkhours()))
            return new ArrayList<WorkHourDTO>();

        return photographer.getWorkhours().stream()
                .map(workhour -> new WorkHourDTO(workhour.getStartTime(), workhour.getEndTime(), workhour.getDay()))
                .collect(Collectors.toList());

    }

    public List<SocialLink> getSocials(Long id) {
        Photographer photographer = findByIdThrow(id);
        if (isNull(photographer.getSocialLinks()))
            return new ArrayList<SocialLink>();

        return photographer.getSocialLinks();

    }

    public Photographer getPhotographerFromUserThrow(User user) {
        // Photographer photographer = photographerRepository.findbyUser(user);
        // if (isNull(photographer))
        Optional<Photographer> photographer = photographerRepository.findByUser(user);
        if (!photographer.isPresent())
            throw new ApiException(HttpStatus.NOT_FOUND, "Photographer not found");
        return photographer.get();
    }

    @Transactional
    public void setWorkHours(Long id, List<WorkHourDTO> workHours) {

        workHours.stream().forEach(newWorkHour -> {
            WorkHour previousWorkHour = workHourService.findForPhotographerAndDay(id, newWorkHour.getDay());
            if (isNull(previousWorkHour)) {
                previousWorkHour = new WorkHour(id, null, null, null, newWorkHour.getDay());
            }
            // to do , should I check more valid hours

            if (!isNull(newWorkHour) && (!isNull(newWorkHour.getStartTime()) && !isNull(newWorkHour.getEndTime()))
                    && newWorkHour.getEndTime().isAfter(newWorkHour.getStartTime())) {
                previousWorkHour.setStartTime(newWorkHour.getStartTime());
                previousWorkHour.setEndTime(newWorkHour.getEndTime());
            }

            // when clearing the day delete work hour
            if (!isNull(newWorkHour) && (isNull(newWorkHour.getStartTime()) && isNull(newWorkHour.getEndTime()))
                    && !isNull(previousWorkHour.getId())) {
                workHourService.delete(previousWorkHour.getId());

            }
        });
    }

    @Transactional
    public List<WorkHourDTO> setWorkHours(Photographer photographer, List<WorkHourDTO> workHours) {
        workHours.stream().forEach(newWorkHour -> {
            // find previous workhour settings for that day,
            // if there are none create one with no hours
            WorkHour previousWorkHour = photographer.getWorkhours().stream()
                    .filter(wh -> wh.getDay() == newWorkHour.getDay()).findFirst().orElse(null);
            if (isNull(previousWorkHour)) {
                previousWorkHour = (new WorkHour(null, photographer, null, null, newWorkHour.getDay()));
                photographer.getWorkhours().add(previousWorkHour);
            }
            if (!isNull(newWorkHour) && (!isNull(newWorkHour.getStartTime()) && !isNull(newWorkHour.getEndTime()))
                    && newWorkHour.getEndTime().isAfter(newWorkHour.getStartTime())) {
                previousWorkHour.setStartTime(newWorkHour.getStartTime());
                previousWorkHour.setEndTime(newWorkHour.getEndTime());
                workHourService.save(previousWorkHour);
                save(photographer);
            }

            // when clearing the day delete work hour
            if (!isNull(newWorkHour) && (isNull(newWorkHour.getStartTime()) && isNull(newWorkHour.getEndTime()))) {
                photographer.getWorkhours().remove(previousWorkHour);

            }

        });
        return photographer.getWorkhours().isEmpty() ? new ArrayList<WorkHourDTO>()
                : photographer.getWorkhours().stream().map(
                        workhour -> new WorkHourDTO(workhour.getStartTime(), workhour.getEndTime(), workhour.getDay()))
                        .toList();
    }

    @Transactional
    public List<SocialLink> updateSocialLinks(Photographer photographer, ArrayList<SocialLink> socialLinks) {

        if (!isNull(socialLinks)) {
            socialLinks.stream().forEach(social -> {
                SocialLink oldSocialLink = photographer.getSocialLinks().stream()
                        .filter(photographerSocial -> photographerSocial.getPlatform() == social.getPlatform())
                        .findFirst()
                        .orElse(new SocialLink(null, photographer, social.getLinkUrl(), social.getPlatform()));
                oldSocialLink.setLinkUrl(social.getLinkUrl());
                oldSocialLink.setPlatform(social.getPlatform());
                socialLinkService.save(oldSocialLink);
                photographer.getSocialLinks().add(oldSocialLink);
            });
        }
        save(photographer);
        return photographer.getSocialLinks();
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
    public PhotographerResponse updateProfile(Photographer photographer, PhotographerRequest photographerRequest) {
        // Photographer photographer = findByIdThrow(photographerId);
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