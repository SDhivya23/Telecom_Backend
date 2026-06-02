package com.example.repositories;

import com.example.entities.Hazard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HazardRepository extends JpaRepository<Hazard, Integer> {
    List<Hazard> findByLocation(String location);
}