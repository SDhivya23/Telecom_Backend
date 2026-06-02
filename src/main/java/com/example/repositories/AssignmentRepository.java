package com.example.repositories;

import com.example.entities.Assignment;
import com.example.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByEngineerEngineerId(Integer id);
    List<Assignment> findByEngineerEngineerIdAndStatus(
            Integer engineerId,
            AssignmentStatus status
    );

    List<Assignment> findByTicketTicketId(Integer ticketId);
    List<Assignment> findByAssignedByUserId(Integer adminId);

}
