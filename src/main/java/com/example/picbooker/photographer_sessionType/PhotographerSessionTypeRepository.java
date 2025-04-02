package com.example.picbooker.photographer_sessionType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.sessionType.SessionTypeName;

@Repository
public interface PhotographerSessionTypeRepository extends JpaRepository<PhotographerSessionType, Long> {

        PhotographerSessionType findFirstByTypeAndPhotographer_Id(SessionTypeName type,
                        Long photographerId);

        int deleteByPhotographer_IdAndType(Long photographerId,
                        SessionTypeName typeName);

        List<PhotographerSessionType> findByPhotographerInAndType(List<Photographer> photographers,
                        SessionTypeName type);

        List<PhotographerSessionType> findByTypeAndPhotographer_User_CityIgnoreCase(SessionTypeName type, String city);

        List<PhotographerSessionType> findByType(
                        SessionTypeName type);
}
