package com.example.services;

import com.example.entities.Hazard;
import com.example.entities.User;
import com.example.repositories.HazardRepository;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HazardService {

    @Autowired
    private HazardRepository hazardRepository;

    @Autowired
    private UserRepository userRepository;


    public Hazard save(Hazard hazard) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        hazard.setCreatedBy(admin);

        hazard.setCreatedAt(LocalDateTime.now());

        return hazardRepository.save(hazard);
    }

    public List<Hazard> getAll() {
        return hazardRepository.findAll();
    }

    public Hazard getById(Integer id) {
        return hazardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hazard not found"));
    }

    public Hazard update(Integer id, Hazard updatedHazard) {

        Hazard existing = hazardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hazard not found"));

        existing.setLocation(updatedHazard.getLocation());
        existing.setLatitude(updatedHazard.getLatitude());
        existing.setLongitude(updatedHazard.getLongitude());
        existing.setDescription(updatedHazard.getDescription());
        existing.setRiskLevel(updatedHazard.getRiskLevel());

        return hazardRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!hazardRepository.existsById(id)) {
            throw new RuntimeException("Hazard not found");
        }
        hazardRepository.deleteById(id);
    }
}
