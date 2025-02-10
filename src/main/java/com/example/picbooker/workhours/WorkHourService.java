package com.example.picbooker.workhours;

import java.time.DayOfWeek;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkHourService {

    @Autowired
    private WorkHourRepository workHourRepository;

    public void create() {
        // to do implement ;
    }

    public Optional<WorkHour> findById(Long id) {
        return workHourRepository.findById(id);
    }

    public WorkHour findByIdThrow(Long id) {
        return workHourRepository.findById(id).orElseThrow();
    }

    public WorkHour save(WorkHour workHour) {
        return workHourRepository.save(workHour);
    }

    public void findForPhotographer() {
        // to do implement ;
        // list of all workhours
    }

    public WorkHour findForPhotographerAndDay(Long photographerId, DayOfWeek day) {

        // specific day workhour
        return workHourRepository.findByDayAndPhotographer_Id(day, photographerId);
    }

    public void delete(WorkHour workHour) {
        workHourRepository.delete(workHour);
    }

    public void delete(Long id) {
        workHourRepository.deleteById(id);
    }

    public void updateForPhotographer() {
        // to do implement ;
        // specific day workhour
    }

    // should I delete or set time as null ? maybe delete
}
