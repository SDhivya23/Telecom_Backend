package com.example.services;

import com.example.entities.*;
import com.example.enums.*;
import com.example.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngineerService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TaskUpdateRepository taskUpdateRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private EngineerRepository engineerRepository;

    public List<Assignment> getTasksByEmail(String email) {
        return assignmentRepository.findAll().stream()
                .filter(a -> a.getEngineer().getUser().getEmail().equals(email))
                .toList();
    }

    public Assignment updateStatus(Integer assignmentId, String status) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Engineer engineer = assignment.getEngineer();

        if (status.equalsIgnoreCase("ACCEPTED")) {

            assignment.setStatus(AssignmentStatus.ACCEPTED);
            assignment.getTicket().setStatus(TicketStatus.IN_PROGRESS);

            ticketRepository.save(assignment.getTicket());

            engineer.setStatus(EngineerStatus.BUSY);
            engineerRepository.save(engineer);

        } else if (status.equalsIgnoreCase("REJECTED")) {

            assignment.setStatus(AssignmentStatus.REJECTED);

            Ticket t = assignment.getTicket();
            t.setStatus(TicketStatus.OPEN);
            ticketRepository.save(t);

            engineer.setStatus(EngineerStatus.AVAILABLE);
            engineerRepository.save(engineer);

            User admin = assignment.getAssignedBy();

            if (admin != null) {
                notificationService.notifyUser(
                        admin.getUserId(),
                        "⚠ Engineer rejected Ticket #" + t.getTicketId()
                );
            }
        }

        return assignmentRepository.save(assignment);
    }

    public void updateTask(Integer assignmentId,
                           String status,
                           String remarks,
                           String email) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.ACCEPTED) {
            throw new RuntimeException("Task must be ACCEPTED before update");
        }

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase().trim());

        TaskUpdate existing = taskUpdateRepository
                .findByAssignmentAssignmentId(assignmentId)
                .orElse(null);

        if (existing != null) {
            existing.setStatus(taskStatus);
            existing.setRemarks(remarks);
            taskUpdateRepository.save(existing);
        } else {
            TaskUpdate taskUpdate = TaskUpdate.builder()
                    .assignment(assignment)
                    .status(taskStatus)
                    .remarks(remarks)
                    .build();

            taskUpdateRepository.save(taskUpdate);
        }

        Ticket ticket = assignment.getTicket();
        User assignedAdmin = assignment.getAssignedBy();
        Engineer engineer = assignment.getEngineer();

        if (taskStatus == TaskStatus.SUCCESS) {

            ticket.setStatus(TicketStatus.COMPLETED);
            ticketRepository.save(ticket);

            emailService.sendCompletedEmail(
                    ticket.getUser().getEmail(),
                    ticket.getUser().getUserId()
            );
        }


        else if (taskStatus == TaskStatus.FAILURE) {

            ticket.setStatus(TicketStatus.FAILED);
            ticketRepository.save(ticket);

            if (assignedAdmin != null) {


                emailService.sendFailedEmailToAdmin(
                        assignedAdmin.getEmail(),
                        ticket.getTicketId(),
                        assignedAdmin.getUserId()
                );

                notificationService.notifyUser(
                        assignedAdmin.getUserId(),
                        "🚨 Ticket #" + ticket.getTicketId() + " FAILED by engineer"
                );
            }
        }


        else if (taskStatus == TaskStatus.DEFERRED) {

            ticket.setStatus(TicketStatus.DEFERRED);
            ticketRepository.save(ticket);

            if (assignedAdmin != null) {

                emailService.sendDeferredEmailToAdmin(
                        assignedAdmin.getEmail(),
                        ticket.getTicketId(),
                        assignedAdmin.getUserId()
                );

                notificationService.notifyUser(
                        assignedAdmin.getUserId(),
                        "⏸ Ticket #" + ticket.getTicketId() + " DEFERRED by engineer"
                );
            }
        }

        int workload = assignmentService.calculateActiveWorkload(engineer.getEngineerId());

        if (workload == 0) {
            engineer.setStatus(EngineerStatus.AVAILABLE);
        } else {
            engineer.setStatus(EngineerStatus.BUSY);
        }

        engineerRepository.save(engineer);
    }
}