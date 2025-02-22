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
import com.example.picbooker.additionalService.AddOnType;
import com.example.picbooker.photographer_additionalService.PhotographerAddOn;
import com.example.picbooker.photographer_additionalService.PhotographerAddOnDTO;
import com.example.picbooker.photographer_additionalService.PhotographerAddOnService;
import com.example.picbooker.photographer_sessionType.PhotographerSessionType;
import com.example.picbooker.photographer_sessionType.PhotographerSessionTypeDTO;
import com.example.picbooker.photographer_sessionType.PhotographerSessionTypeService;
import com.example.picbooker.review.Review;
import com.example.picbooker.review.ReviewService;
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
    private PhotographerAddOnService photographerAddOnService;

    @Autowired
    private PhotographerSessionTypeService photographerSessionTypeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

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

    public Optional<Photographer> getPhotographerFromUser(User user) {
        // Photographer photographer = photographerRepository.findbyUser(user);
        // if (isNull(photographer))
        Optional<Photographer> photographer = photographerRepository.findByUser(user);
        return photographer;
    }

    // not used currently
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
                // previousWorkHour.setPhotographer(null);
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

    public List<Review> getReviews(Long photographerId) {
        // to do paginate
        return reviewService.findForPhotographer(photographerId);

    }

    public List<Review> getReviews(Photographer photographer) {
        // to do map them , paginate
        return photographer.getReviews();
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
        if (!isNull(photographerRequest.getPersonalName()))
            photographer.setPersonalName(photographerRequest.getPersonalName());
        return PhotographerMapper.toResponse(photographer);

    }

    public List<PhotographerSessionType> getSessionTypes(Long id) {
        return findByIdThrow(id).getSessionTypes();
    }

    public PhotographerSessionType updateSessionType(Photographer photographer, Long photographerSessionTypeId,
            PhotographerSessionTypeDTO request) {
        PhotographerSessionType photographerSessionType = photographerSessionTypeService
                .findByIdThrow(photographerSessionTypeId);
        if (photographerSessionType.getPhotographer().getId() != photographer.getId())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Not your resource");
        return photographerSessionTypeService.updatePhotographerSessionType(photographerSessionType, request);
    }

    public PhotographerAddOn createAddOn(Photographer photographer, PhotographerAddOnDTO request) {

        return photographerAddOnService.addAddOn(photographer, request);

    }

    public List<PhotographerAddOn> getAddOnsForPhotographer(Long id) {
        return findByIdThrow(id).getAdditionalServices();
    }

    public PhotographerAddOn updateAddOn(Photographer photographer, Long addOnId, PhotographerAddOnDTO request) {
        PhotographerAddOn addOn = getAddOnByIdThrow(addOnId);
        if (addOn.getPhotographer().getId() != photographer.getId())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Not your resource");
        return photographerAddOnService.updateAddOn(addOn, request);
    }

    @Transactional
    public void deleteAddOnById(Photographer photographer, Long addOnId) {
        PhotographerAddOn addOn = photographerAddOnService.findByIdThrow(addOnId);
        if (photographer.getId() != addOn.getPhotographer().getId())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unauthorized delete");
        photographer.getAdditionalServices().remove(addOn);
        photographerAddOnService.deleteById(addOnId);
    }

    public PhotographerAddOn getAddOnByIdThrow(Long addOnId) {
        return photographerAddOnService.findByIdThrow(addOnId);
    }

    public PhotographerAddOn getAddOnByNameAndPhotographer(Long photographerId, AddOnType type) {
        return photographerAddOnService.findForPhotographerAndAddOn(photographerId, type);
    }

    // function to create custom private session and generate link to send to client

    // get photos from instagram integration
}