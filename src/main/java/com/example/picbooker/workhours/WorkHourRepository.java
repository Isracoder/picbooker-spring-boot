package com.example.picbooker.workhours;

import java.time.DayOfWeek;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {

    WorkHour findByDayAndPhotographer_Id(DayOfWeek day, Long photographerId);
}
