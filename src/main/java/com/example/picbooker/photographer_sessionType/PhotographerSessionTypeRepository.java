package com.example.picbooker.photographer_sessionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.picbooker.sessionType.SessionTypeName;

@Repository
public interface PhotographerSessionTypeRepository extends JpaRepository<PhotographerSessionType, Long> {

        PhotographerSessionType findFirstByTypeAndPhotographer_Id(SessionTypeName type,
                        Long photographerId);

        int deleteByPhotographer_IdAndType(Long photographerId,
                        SessionTypeName typeName);
}
