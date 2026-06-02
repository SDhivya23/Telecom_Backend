package com.example.controllers;

import com.example.entities.Hazard;
import com.example.services.HazardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/api/admin/hazards")
public class HazardController {

    @Autowired
    private HazardService hazardService;

    @PostMapping
    public Hazard create(@RequestBody Hazard hazard) {
        return hazardService.save(hazard);
    }

    @GetMapping
    public List<Hazard> getAll() {
        return hazardService.getAll();
    }

    @GetMapping("/{id}")
    public Hazard getById(@PathVariable Integer id) {
        return hazardService.getById(id);
    }

    @PutMapping("/{id}")
    public Hazard update(@PathVariable Integer id,
                         @RequestBody Hazard hazard) {
        return hazardService.update(id, hazard);
    }


    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        hazardService.delete(id);
        return "Hazard deleted successfully";
    }
}