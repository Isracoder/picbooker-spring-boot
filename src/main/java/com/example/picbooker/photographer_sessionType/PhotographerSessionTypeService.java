package com.example.picbooker.photographer_sessionType;

import static java.util.Objects.isNull;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.sessionType.SessionTypeName;

import jakarta.transaction.Transactional;

@Service
public class PhotographerSessionTypeService {

    @Autowired
    private PhotographerSessionTypeRepository photographerSessionTypeRepository;

    public PhotographerSessionType create(Photographer photographer, SessionTypeName sessionType, Double perHour,
            Currency currency, Integer duration, String description, String location, Boolean keepPrivate,
            Boolean requiresDeposit, Double depositAmount, String customSessionType) {

        return PhotographerSessionType.builder()
                .currency(currency)
                .description(description)
                .type(sessionType)
                .durationMinutes(duration)
                .photographer(photographer)
                .pricePerDuration(perHour)
                .location(location)
                .customSessionType(customSessionType)
                .isPrivate(keepPrivate)
                .requiresDeposit(requiresDeposit)
                .depositAmount(depositAmount)
                .build();

    }

    public Optional<PhotographerSessionType> findById(Long id) {
        return photographerSessionTypeRepository.findById(id);
    }

    public PhotographerSessionType findByIdThrow(Long id) {
        return photographerSessionTypeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Photographer session type not found"));
    }

    public PhotographerSessionType save(PhotographerSessionType photographerSessionType) {
        return photographerSessionTypeRepository.save(photographerSessionType);
    }

    public PhotographerSessionType createAndSave(Photographer photographer, SessionTypeName sessionType, Double perHour,
            Currency currency, Integer duration, String description, String location, Boolean keepPrivate,
            Boolean requiresDeposit, Double depositAmount, String customSessionType) {
        System.out.println("photographer id : " + photographer.getId());
        return save(create(photographer, sessionType, perHour, currency, duration, description, location, keepPrivate,
                requiresDeposit, depositAmount, customSessionType));
    }

    public PhotographerSessionType findForPhotographerAndSessionType(Long photographerId, SessionTypeName type) {

        return photographerSessionTypeRepository.findFirstByTypeAndPhotographer_Id(type, photographerId);
    }

    public List<PhotographerSessionType> findByPhotographerInListAndType(List<Photographer> photographers,
            SessionTypeName type) {
        return photographerSessionTypeRepository.findByPhotographerInAndType(photographers, type);
    }

    // to think paginate these 2
    public List<PhotographerSessionType> findByPhotographerCityAndType(String city,
            SessionTypeName type) {
        return photographerSessionTypeRepository.findByTypeAndPhotographer_User_CityIgnoreCase(type, city.trim());
    }

    public List<PhotographerSessionType> findByType(SessionTypeName type) {
        return photographerSessionTypeRepository.findByType(type);
    }

    public PhotographerSessionType addSessionType(Photographer photographer,
            PhotographerSessionTypeDTO photographerSessionTypeDTO) {
        if (isNull(photographer))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Photographer can't be null");
        // TODO: check necessity of persisting from both sides for review, sessiontype,
        // addon
        return createAndSave(photographer, photographerSessionTypeDTO.getType(),
                photographerSessionTypeDTO.getPricePerDuration(),
                Currency.getInstance(photographerSessionTypeDTO.getCurrencyCode()),
                photographerSessionTypeDTO.getDurationMinutes(), photographerSessionTypeDTO.getDescription(),
                photographerSessionTypeDTO.getLocation(), photographerSessionTypeDTO.getIsPrivate(),
                photographerSessionTypeDTO.getRequiresDeposit(), photographerSessionTypeDTO.getDepositAmount(),
                photographerSessionTypeDTO.getCustomSessionType());

    }

    @Transactional
    public void deleteByName(Long photographerId, SessionTypeName typeName) {
        photographerSessionTypeRepository.deleteByPhotographer_IdAndType(photographerId, typeName);
    }

    @Transactional
    private void deleteById(Long id) {
        photographerSessionTypeRepository.deleteById(id);
    }

    @Transactional
    public void deleteById(Photographer photographer, Long sessionTypeid) {
        PhotographerSessionType sessionType = findByIdThrow(sessionTypeid);
        if (sessionType.getPhotographer().getId() != photographer.getId())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Not yours, No deletion authority");
        photographer.getSessionTypes().remove(sessionType);
        System.out.println("session id :" + sessionTypeid);
        deleteById(sessionTypeid);
    }

    @Transactional
    public PhotographerSessionType updatePhotographerSessionType(PhotographerSessionType photographerSessionType,
            PhotographerSessionTypeDTO request) {

        // to think
        // should changing the type create another one to prevent inconsistency with
        // past bookings ?
        if (!isNull(request.getCurrencyCode()))
            photographerSessionType.setCurrency(Currency.getInstance(request.getCurrencyCode()));
        if (!isNull(request.getDurationMinutes()))
            photographerSessionType
                    .setDurationMinutes(request.getDurationMinutes());
        if (!isNull(request.getPricePerDuration()))
            photographerSessionType.setPricePerDuration(request.getPricePerDuration());

        if (!isNull(request.getType()))
            photographerSessionType.setType(request.getType());
        if (!isNull(request.getCustomSessionType()))
            photographerSessionType.setCustomSessionType(request.getCustomSessionType());
        if (!isNull(request.getDepositAmount()))
            photographerSessionType.setDepositAmount(request.getDepositAmount());
        if (!isNull(request.getDescription()))
            photographerSessionType.setDescription(request.getDescription());

        if (!isNull(request.getIsPrivate()))
            photographerSessionType.setIsPrivate(request.getIsPrivate());
        if (!isNull(request.getLocation()))
            photographerSessionType.setLocation(request.getLocation());
        if (!isNull(request.getRequiresDeposit()))
            photographerSessionType.setRequiresDeposit(request.getRequiresDeposit());

        // to add validation rules
        if (isNull(photographerSessionType.getRequiresDeposit()) || !photographerSessionType.getRequiresDeposit())
            photographerSessionType.setDepositAmount(0.0D);
        if (photographerSessionType.getType() != SessionTypeName.OTHER)
            photographerSessionType.setCustomSessionType(null);

        return (photographerSessionType);

    }

}