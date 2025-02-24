package com.example.picbooker.photographer_additionalService;

import static java.util.Objects.isNull;

import java.util.Currency;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.picbooker.ApiException;
import com.example.picbooker.additionalService.AddOnType;
import com.example.picbooker.photographer.Photographer;

import jakarta.transaction.Transactional;

@Service
public class PhotographerAddOnService {

    @Autowired
    private PhotographerAddOnRepository photographerAddOnRepository;

    public Optional<PhotographerAddOn> findById(Long id) {
        return photographerAddOnRepository.findById(id);
    }

    @Transactional
    public PhotographerAddOn findByIdThrow(Long id) {
        return photographerAddOnRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Not found"));
    }

    public PhotographerAddOn create(Photographer photographer, AddOnType addOnType, Double perHour,
            Currency currency, String description, Boolean multPerSession, String customSessionType,
            Boolean isPrivate, Boolean isSpecialOffer, Date endDate) {
        return new PhotographerAddOn(null, photographer, addOnType, customSessionType, multPerSession, description,
                perHour, isPrivate, isSpecialOffer, endDate, currency);

    }

    public PhotographerAddOn save(PhotographerAddOn photographerAddOn) {
        return photographerAddOnRepository.save(photographerAddOn);
    }

    public PhotographerAddOn createAndSave(Photographer photographer, AddOnType addOnType, Double perHour,
            Currency currency, String description, Boolean multPerSession, String customSessionType,
            Boolean isPrivate, Boolean isSpecialOffer, Date endDate) {
        return save(create(photographer, addOnType, perHour, currency, description,
                multPerSession, customSessionType, isPrivate, isSpecialOffer, endDate));
    }

    public PhotographerAddOn findForPhotographerAndAddOn(Long photographerId, AddOnType type) {

        return photographerAddOnRepository.findFirstByTypeAndPhotographer_Id(type, photographerId);
    }

    public PhotographerAddOn addAddOn(Photographer photographer,
            PhotographerAddOnDTO photographerAddOnDTO) {

        return createAndSave(photographer, photographerAddOnDTO.getType(), photographerAddOnDTO.getFee(),
                Currency.getInstance(photographerAddOnDTO.getCurrencyCode()), photographerAddOnDTO.getDescription(),
                photographerAddOnDTO.getMultipleAllowedInSession(), photographerAddOnDTO.getCustomSessionType(),
                photographerAddOnDTO.getIsPrivate(), photographerAddOnDTO.getIsSpecialOffer(),
                photographerAddOnDTO.getEndDate());

    }

    public void deleteByName(Long photographerId, AddOnType typeName) {
        photographerAddOnRepository.deleteByPhotographer_IdAndType(photographerId, typeName);
    }

    @Transactional
    public void deleteById(Long id) {
        photographerAddOnRepository.deleteById(id);
    }

    @Transactional
    public PhotographerAddOn updateAddOn(PhotographerAddOn photographerAddOn, PhotographerAddOnDTO request) {

        if (!isNull(request.getCurrencyCode()))
            photographerAddOn.setCurrency(Currency.getInstance(request.getCurrencyCode()));
        if (!isNull(request.getDescription()))
            photographerAddOn
                    .setDescription(request.getDescription());
        if (!isNull(request.getFee()))
            photographerAddOn.setFee(request.getFee());
        if (!isNull(request.getMultipleAllowedInSession()))
            photographerAddOn.setMultipleAllowedInSession(request.getMultipleAllowedInSession());

        if (!isNull(request.getType()))
            photographerAddOn.setType(request.getType());
        if (!isNull(request.getCustomSessionType()))
            photographerAddOn.setCustomSessionType(request.getCustomSessionType());

        if (!isNull(request.getIsPrivate()))
            photographerAddOn.setIsPrivate(request.getIsPrivate());

        if (!isNull(request.getIsSpecialOffer()))
            photographerAddOn.setIsSpecialOffer(request.getIsSpecialOffer());

        if (!isNull(request.getEndDate()))
            photographerAddOn.setEndDate(request.getEndDate());

        // validation
        if (photographerAddOn.getType() != AddOnType.OTHER)
            photographerAddOn.setCustomSessionType(null);
        return (photographerAddOn);

    }
}