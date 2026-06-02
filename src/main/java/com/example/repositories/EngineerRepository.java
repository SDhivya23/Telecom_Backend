package com.example.repositories;

import com.example.entities.Engineer;
import com.example.enums.EngineerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EngineerRepository extends JpaRepository<Engineer, Integer> {
    List<Engineer> findByStatus(EngineerStatus status);
    Engineer findByUser_UserId(Integer userId);

}