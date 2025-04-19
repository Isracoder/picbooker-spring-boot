package com.example.picbooker.blocked_time;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedTimeRepository extends JpaRepository<BlockedTime, Long> {

        List<BlockedTime> findByPhotographer_IdAndEndDateTimeAfter(Long photographerId, LocalDateTime dateTime);

        @Query("SELECT b FROM BlockedTime b WHERE b.photographer.id = :photographerId " +
                        "AND b.startDateTime < :endDateTime " +
                        "AND b.endDateTime > :startDateTime")
        List<BlockedTime> findByPhotographerIdAndOverlapping(@Param("photographerId") Long photographerId,
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime);

}
