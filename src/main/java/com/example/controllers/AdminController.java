// FILE: com/example/controllers/AdminController.java  (FULL REPLACEMENT)
package com.example.controllers;

import com.example.entities.Assignment;
import com.example.entities.Engineer;
import com.example.entities.Ticket;
import com.example.enums.TicketStatus;
import com.example.enums.UserRole;
import com.example.repositories.TicketRepository;
import com.example.repositories.UserRepository;
import com.example.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private AssignmentService assignmentService;
    @Autowired private TicketRepository  ticketRepository;
    @Autowired private UserRepository    userRepository;

    @PostMapping("/assign")
    public Assignment assign(@RequestParam Integer ticketId, @RequestParam Integer engineerId) {
        return assignmentService.assign(ticketId, engineerId);
    }

    @PostMapping("/smart-assign")
    public Assignment smartAssign(@RequestParam Integer ticketId) {
        return assignmentService.smartAssign(ticketId);
    }

    @PutMapping("/reassign")
    public Assignment reassign(@RequestParam Integer ticketId, @RequestParam Integer engineerId) {
        return assignmentService.assign(ticketId, engineerId);
    }

    @GetMapping("/dashboard/{adminId}")
    public Map<String, Object> dashboard(@PathVariable Integer adminId) {
        Map<String, Object> map = new HashMap<>();
        map.put("totalTickets",      ticketRepository.countByAdminUserId(adminId));
        map.put("openTickets",       ticketRepository.countByStatusAndAdminUserId(TicketStatus.OPEN,        adminId));
        map.put("assignedTickets",   ticketRepository.countByStatusAndAdminUserId(TicketStatus.ASSIGNED,    adminId));
        map.put("inProgressTickets", ticketRepository.countByStatusAndAdminUserId(TicketStatus.IN_PROGRESS, adminId));
        map.put("completedTickets",  ticketRepository.countByStatusAndAdminUserId(TicketStatus.COMPLETED,   adminId));
        map.put("failedTickets",     ticketRepository.countByStatusAndAdminUserId(TicketStatus.FAILED,      adminId));
        map.put("deferredTickets",   ticketRepository.countByStatusAndAdminUserId(TicketStatus.DEFERRED,    adminId));
        return map;
    }

    @GetMapping("/open-tickets/{adminId}")
    public List<Ticket> getOpenTickets(@PathVariable Integer adminId) {
        return ticketRepository.findByStatusAndAdminUserId(TicketStatus.OPEN, adminId);
    }

    // Assignments made by this admin
    @GetMapping("/assignments/{adminId}")
    public List<Assignment> getAssignmentsByAdmin(@PathVariable Integer adminId) {
        return assignmentService.getAssignmentsByAdmin(adminId);
    }

    @GetMapping("/admins")
    public List<Map<String, Object>> getAdmins() {
        return userRepository.findByRole(UserRole.ADMIN).stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", u.getUserId());
            m.put("name",   u.getName());
            m.put("email",  u.getEmail());
            return m;
        }).toList();
    }

    @GetMapping("/available-engineers")
    public List<Map<String, Object>> getAvailableEngineers() {
        List<Engineer> engineers = assignmentService.getAvailableEngineers();
        engineers.sort(Comparator.comparingInt(e -> assignmentService.getWorkload(e.getEngineerId())));
        return engineers.stream().map(engineer -> {
            Map<String, Object> map = new HashMap<>();
            map.put("engineerId", engineer.getEngineerId());
            map.put("name",       engineer.getUser().getName());
            map.put("email",      engineer.getUser().getEmail());
            map.put("experience", engineer.getExperience());
            map.put("skillSet",   engineer.getSkillSet());
            map.put("workload",   assignmentService.getWorkload(engineer.getEngineerId()));
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/engineers-on-leave")
    public List<Map<String, Object>> engineersOnLeave() {
        return assignmentService.getAllEngineersOnHoliday().stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("engineerId", e.getEngineerId());
            m.put("name",       e.getUser().getName());
            m.put("email",      e.getUser().getEmail());
            return m;
        }).toList();
    }
}