package com.example.picbooker.socialLinks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {
    // SocialLink findByDayAndPhotographer_Id(DayOfWeek day, Long photographerId);
}
