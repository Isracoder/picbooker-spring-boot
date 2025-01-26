package com.example.picbooker.additionalService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Long> {
    Boolean existsByType(AdditionalServiceType type);
}
