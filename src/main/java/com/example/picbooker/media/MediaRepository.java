package com.example.picbooker.media;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query("SELECT m FROM Media m WHERE m.photographer.id = :photographerId AND (m.mediaType = 'PHOTO' OR m.mediaType = 'VIDEO')")
    Page<Media> findPortfolioByPhotographer(@Param("photographerId") Long photographerId, Pageable pageable);

    long countByPhotographer_Id(Long photographerId);

    @Query("SELECT m FROM Media m WHERE m.photographer.id = :photographerId AND m.mediaType = 'PROFILE_PICTURE'")
    Media findProfilePictureByPhotographer(@Param("photographerId") Long photographerId);

}
