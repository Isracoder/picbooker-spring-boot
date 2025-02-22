package com.example.picbooker.photographer_additionalService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.picbooker.additionalService.AddOnType;

@Repository
public interface PhotographerAddOnRepository extends JpaRepository<PhotographerAddOn, Long> {

    PhotographerAddOn findFirstByTypeAndPhotographer_Id(AddOnType type, Long photographerId);

    int deleteByPhotographer_IdAndType(Long photographerId,
            AddOnType type);

}
