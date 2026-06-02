package com.example.repositories;

import com.example.entities.TaskUpdate;
import com.example.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskUpdateRepository extends JpaRepository<TaskUpdate, Integer> {

    boolean existsByAssignmentAssignmentId(Integer assignmentId);

    boolean existsByAssignmentAssignmentIdAndStatusIn(
            Integer assignmentId,
            List<TaskStatus> statuses
    );

    Optional<TaskUpdate> findByAssignmentAssignmentId(Integer assignmentId);
}