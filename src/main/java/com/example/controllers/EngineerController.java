package com.example.controllers;

import com.example.entities.Assignment;
import com.example.services.EngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/api/engineer")
public class EngineerController {

    @Autowired
    private EngineerService engineerService;


    @GetMapping("/tasks")
    public List<Assignment> getMyTasks(Authentication auth) {

        String email = auth.getName();

        return engineerService.getTasksByEmail(email);
    }

    @PutMapping("/tasks/{assignmentId}/status")
    public Assignment updateStatus(@PathVariable Integer assignmentId,
                                   @RequestParam String status) {

        return engineerService.updateStatus(assignmentId, status);
    }


    @PostMapping("/tasks/{assignmentId}/update")
    public String updateTask(@PathVariable Integer assignmentId,
                             @RequestParam String status,
                             @RequestParam String remarks,
                             Authentication auth) {

        String email = auth.getName();

        engineerService.updateTask(assignmentId, status, remarks, email);

        return "Task updated successfully";
    }
}