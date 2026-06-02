package com.example.services;

import com.example.entities.*;
import com.example.enums.*;
import com.example.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.example.utils.DistanceUtil;

@Service
public class AssignmentService {

    private final int MAX_TASKS = 10;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskUpdateRepository taskUpdateRepository;

    @Autowired
    private HazardRepository hazardRepository;

    public Assignment assign(Integer ticketId, Integer engineerId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        Engineer newEngineer = engineerRepository.findById(engineerId).orElseThrow();

        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(adminEmail).orElseThrow();


        List<Assignment> existingAssignments =
                assignmentRepository.findByTicketTicketId(ticketId);

        for (Assignment oldAssignment : existingAssignments) {

            if (oldAssignment.getStatus() == AssignmentStatus.ACCEPTED) {

                oldAssignment.setStatus(AssignmentStatus.REJECTED);
                assignmentRepository.save(oldAssignment);

                Engineer oldEngineer = oldAssignment.getEngineer();
                oldEngineer.setStatus(EngineerStatus.AVAILABLE);
                engineerRepository.save(oldEngineer);
            }
        }

        Assignment assignment = Assignment.builder()
                .ticket(ticket)
                .engineer(newEngineer)
                .assignedBy(admin)
                .status(AssignmentStatus.PENDING)
                .build();


        ticket.setStatus(TicketStatus.ASSIGNED);
        ticketRepository.save(ticket);


        newEngineer.setStatus(EngineerStatus.BUSY);
        engineerRepository.save(newEngineer);

        Assignment saved = assignmentRepository.save(assignment);


        List<Hazard> hazards = hazardRepository.findByLocation(ticket.getLocation());

        if (!hazards.isEmpty()) {

            Hazard h = hazards.get(0);

            String hazardMessage =
                    "⚠ HAZARD ALERT!\n" +
                            "Location: " + h.getLocation() + "\n" +
                            "Risk Level: " + h.getRiskLevel() + "\n" +
                            "Description: " + h.getDescription();

            emailService.sendHazardAwareAssignmentEmail(
                    newEngineer.getUser().getEmail(),
                    newEngineer.getUser().getName(),
                    ticket.getTicketId(),
                    newEngineer.getEngineerId(),
                    hazardMessage
            );

        } else {

            emailService.sendTicketAssignedEmail(
                    newEngineer.getUser().getEmail(),
                    newEngineer.getUser().getName(),
                    ticket.getTicketId(),
                    newEngineer.getEngineerId()
            );
        }

        return saved;
    }


    public Assignment smartAssign(Integer ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();

        List<Engineer> engineers = engineerRepository.findAll();

        Engineer bestEngineer = null;
        double bestScore = Double.MAX_VALUE;

        for (Engineer engineer : engineers) {

            boolean isHoliday = holidayRepository
                    .existsByEngineerEngineerIdAndHolidayDate(
                            engineer.getEngineerId(),
                            LocalDate.now()
                    );

            if (isHoliday) continue;

            if (engineer.getHomeLat() == null || engineer.getHomeLng() == null ||
                    ticket.getLatitude() == null || ticket.getLongitude() == null) {
                continue;
            }

            int workload = calculateActiveWorkload(engineer.getEngineerId());

            if (workload >= MAX_TASKS) continue;

            double distance = DistanceUtil.calculate(
                    engineer.getHomeLat(),
                    engineer.getHomeLng(),
                    ticket.getLatitude(),
                    ticket.getLongitude()
            );

            double score = distance + (workload * 2);

            if (score < bestScore) {
                bestScore = score;
                bestEngineer = engineer;
            }
        }

        if (bestEngineer == null) {
            throw new RuntimeException("No available engineer");
        }

        return assign(ticketId, bestEngineer.getEngineerId());
    }

    public List<Engineer> getAvailableEngineers() {

        List<Engineer> engineers = engineerRepository.findAll();
        List<Engineer> availableEngineers = new ArrayList<>();

        for (Engineer engineer : engineers) {

            boolean isHoliday = holidayRepository
                    .existsByEngineerEngineerIdAndHolidayDate(
                            engineer.getEngineerId(),
                            LocalDate.now()
                    );

            if (isHoliday) continue;

            int workload = calculateActiveWorkload(engineer.getEngineerId());

            if (workload >= MAX_TASKS) continue;

            availableEngineers.add(engineer);
        }

        return availableEngineers;
    }


    public int calculateActiveWorkload(Integer engineerId) {

        List<Assignment> assignments =
                assignmentRepository.findByEngineerEngineerIdAndStatus(
                        engineerId,
                        AssignmentStatus.ACCEPTED
                );

        int workload = 0;

        for (Assignment a : assignments) {

            TaskUpdate update = taskUpdateRepository
                    .findByAssignmentAssignmentId(a.getAssignmentId())
                    .orElse(null);

            boolean completed = (update != null) &&
                    (update.getStatus().equals(TaskStatus.SUCCESS) ||
                            update.getStatus().equals(TaskStatus.FAILURE) ||
                            update.getStatus().equals(TaskStatus.DEFERRED));

            if (!completed) {
                workload++;
            }
        }

        return workload;
    }

    public List<Engineer> getAllEngineersOnHoliday() {

        List<Holiday> holidays =
                holidayRepository.findByHolidayDate(LocalDate.now());

        return holidays.stream()
                .map(Holiday::getEngineer)
                .toList();
    }

    public List<Assignment> getAssignmentsByAdmin(Integer adminId) {
        return assignmentRepository.findAll().stream()
                .filter(a -> a.getAssignedBy() != null &&
                        a.getAssignedBy().getUserId().equals(adminId))
                .toList();
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }


    public int getWorkload(Integer engineerId) {
        return calculateActiveWorkload(engineerId);
    }
}