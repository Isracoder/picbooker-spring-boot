package com.example.picbooker.photographer;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.picbooker.ApiException;
import com.example.picbooker.additionalService.AddOnType;
import com.example.picbooker.blocked_time.BlockedTime;
import com.example.picbooker.blocked_time.BlockedTimeDTO;
import com.example.picbooker.blocked_time.BlockedTimeRepository;
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

@Service
public class PhotographerService {

    @Autowired
    private PhotographerRepository photographerRepository;

    @Autowired
    private WorkHourService workHourService;

    @Autowired
    private BlockedTimeRepository blockedTimeRepository;

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
        if (isNull(photographer.getWorkHours()))
            return new ArrayList<WorkHourDTO>();

        return photographer.getWorkHours().stream()
                .map(workHour -> new WorkHourDTO(workHour.getStartTime(), workHour.getEndTime(), workHour.getDay()))
                .collect(Collectors.toList());

    }

    public List<SocialLink> getSocials(Long id) {
        Photographer photographer = findByIdThrow(id);
        if (isNull(photographer.getSocialLinks()))
            return new ArrayList<SocialLink>();

        return photographer.getSocialLinks();

    }

    public Photographer getPhotographerFromUserThrow(User user) {

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
            // find previous workHour settings for that day,
            // if there are none create one with no hours
            WorkHour previousWorkHour = photographer.getWorkHours().stream()
                    .filter(wh -> wh.getDay() == newWorkHour.getDay()).findFirst().orElse(null);
            if (isNull(previousWorkHour)) {
                previousWorkHour = (new WorkHour(null, photographer, null, null, newWorkHour.getDay()));
                photographer.getWorkHours().add(previousWorkHour);
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
                photographer.getWorkHours().remove(previousWorkHour);
                // previousWorkHour.setPhotographer(null);
            }

        });
        return photographer.getWorkHours().isEmpty() ? new ArrayList<WorkHourDTO>()
                : photographer.getWorkHours().stream().map(
                        workHour -> new WorkHourDTO(workHour.getStartTime(), workHour.getEndTime(), workHour.getDay()))
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

    @Transactional
    public void updateMinimumNotice(Photographer photographer, Integer minutes) {
        if (isNull(minutes) || minutes < 1440)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid minutes value");
        photographer.setMinimumNoticeBeforeSessionMinutes(minutes);
        save(photographer);
    }

    @Transactional
    public void updateBufferTime(Photographer photographer, Integer minutes) {
        if (isNull(minutes) || minutes < 15)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid minutes value");
        photographer.setBufferTimeMinutes(minutes);
        save(photographer);
    }

    public BlockedTimeDTO blockOutTime(Long photographerId, LocalDateTime blockStart, LocalDateTime blockEnd) {

        // LocalDateTime blockStart = LocalDateTime.of(startDate, startTime);
        // LocalDateTime blockEnd = LocalDateTime.of(endDate, endTime);
        if (blockStart.isAfter(blockEnd))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid block times");
        Photographer photographer = findByIdThrow(photographerId);

        BlockedTime block = new BlockedTime();
        block.setPhotographer(photographer);
        block.setStartDateTime(blockStart);
        block.setEndDateTime(blockEnd);

        return toBlockedTimeDTO(blockedTimeRepository.save(block));
    }

    @Transactional
    public void deleteBlockedOutTime(long blockId, Long photographerId) {
        try {
            BlockedTime blockedTime = blockedTimeRepository.findById(blockId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Time block not found"));
            Photographer photographer = blockedTime.getPhotographer();
            if (photographer.getId() != photographerId) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }

            blockedTimeRepository.delete(blockedTime);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong during deletion");
        }

    }

    public List<BlockedTimeDTO> getUpcomingBlockedTime(Long photographerId) {
        // Photographer photographer = findByIdThrow(photographerId);

        List<BlockedTime> blockedTimeSlots = new ArrayList<>();
        blockedTimeSlots = blockedTimeRepository.findByPhotographer_IdAndEndDateAfter(photographerId,
                LocalDateTime.now());

        return blockedTimeSlots.stream()
                .map(this::toBlockedTimeDTO)
                .collect(Collectors.toList());

    }

    public BlockedTimeDTO toBlockedTimeDTO(BlockedTime blockedTime) {
        return new BlockedTimeDTO(blockedTime.getPhotographer().getId(),
                blockedTime.getPhotographer().getPersonalName(), blockedTime.getStartDateTime(),
                blockedTime.getEndDateTime());
    }

    public Page<Review> getReviews(Long photographerId, Pageable pageable) {
        return reviewService.findForPhotographer(photographerId, pageable);

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

    public List<BlockedTime> findByPhotographerIdAndOverlapping(Long photographerId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return blockedTimeRepository.findByPhotographerIdAndOverlapping(photographerId, startDateTime, endDateTime);
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
        System.out.println(photographer.getAdditionalServices().size());
        photographer.getAdditionalServices().remove(addOn);

        photographerRepository.save(photographer);
    }

    public PhotographerAddOn getAddOnByIdThrow(Long addOnId) {
        return photographerAddOnService.findByIdThrow(addOnId);
    }

    public PhotographerAddOn getAddOnByNameAndPhotographer(Long photographerId, AddOnType type) {
        return photographerAddOnService.findForPhotographerAndAddOn(photographerId, type);
    }

    public ProfileCompletionDTO getProfileCompletion(Long photographerId) {
        Photographer photographer = findByIdThrow(photographerId);
        User user = photographer.getUser();
        Boolean profilePictureSet = photographer.getProfilePhotoUrl() != null;
        Boolean locationSet = user.getCountry() != null && user.getCity() != null;
        // to do check if has valid workHours , and if has non-private session types
        Boolean workHoursSet = !isNull(photographer.getWorkHours()) && photographer.getWorkHours().size() > 0;
        Boolean sessionTypesSet = !isNull(photographer.getSessionTypes()) && photographer.getSessionTypes().size() > 0;
        // should check if not just profile
        Boolean portfolioSet = (photographer.getMediaUploads() != null && photographer.getMediaUploads().size() > 1);

        Boolean socialMediaSet = photographer.getSocialLinks() != null;
        Boolean emailVerified = user.getIsEmailVerified();
        Boolean bioSet = photographer.getBio() != null;

        int listOfThings = ProfileCompletionDTO.getNumberOfFields() - 1;
        int complete = (profilePictureSet ? 1 : 0) + (sessionTypesSet ? 1 : 0) + (workHoursSet ? 1 : 0)
                + (socialMediaSet ? 1 : 0) + (portfolioSet ? 1 : 0) + (locationSet ? 1 : 0) + (emailVerified ? 1 : 0)
                + (bioSet ? 1 : 0);
        System.out.println(complete + "/" + listOfThings);
        return new ProfileCompletionDTO((complete * 1.0) / listOfThings, profilePictureSet, workHoursSet,
                sessionTypesSet,
                socialMediaSet, portfolioSet, locationSet, emailVerified, bioSet);
    }

    public List<Photographer> findByCity(String city) {
        return photographerRepository.findByUser_CityIgnoreCase(city);
    }

    public String uploadProfilePhoto(MultipartFile file) {
        // to do implement ;
        return null;
    }
    // function to create custom private session and generate link to send to client

    // get photos from instagram integration
}