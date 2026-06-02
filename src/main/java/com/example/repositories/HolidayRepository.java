package com.example.repositories;

import com.example.entities.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    boolean existsByEngineerEngineerIdAndHolidayDate(Integer engineerId, LocalDate holidayDate);

    List<Holiday> findByHolidayDate(LocalDate holidayDate);

    List<Holiday> findByEngineerEngineerId(Integer engineerId);
}