package com.example.controllers;

import com.example.entities.Engineer;
import com.example.entities.Holiday;
import com.example.repositories.EngineerRepository;
import com.example.repositories.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    @PostMapping
    public Holiday addHoliday(@RequestParam Integer engineerId,
                              @RequestParam String holidayDate,
                              @RequestParam(required = false) String description) {

        Engineer engineer = engineerRepository.findById(engineerId).orElseThrow();

        LocalDate leaveDate = LocalDate.parse(holidayDate);

        if (holidayRepository.existsByEngineerEngineerIdAndHolidayDate(engineerId, leaveDate)) {
            throw new RuntimeException("Holiday already exists for this date");
        }

        Holiday holiday = Holiday.builder()
                .engineer(engineer)
                .date(LocalDate.now())
                .holidayDate(leaveDate)
                .description(description)
                .build();

        return holidayRepository.save(holiday);
    }

    @PutMapping("/{id}")
    public Holiday updateHoliday(@PathVariable Integer id,
                                 @RequestParam String holidayDate,
                                 @RequestParam(required = false) String description) {

        Holiday holiday = holidayRepository.findById(id).orElseThrow();

        holiday.setHolidayDate(LocalDate.parse(holidayDate));
        holiday.setDescription(description);

        return holidayRepository.save(holiday);
    }


    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {

        holidayRepository.deleteById(id);
        return "Holiday deleted";
    }


    @GetMapping("/all")
    public List<Holiday> getAll() {
        return holidayRepository.findAll();
    }


    @GetMapping("/today")
    public List<Engineer> getTodayHolidayEngineers() {

        List<Holiday> holidays =
                holidayRepository.findByHolidayDate(LocalDate.now());

        return holidays.stream()
                .map(Holiday::getEngineer)
                .toList();
    }
}