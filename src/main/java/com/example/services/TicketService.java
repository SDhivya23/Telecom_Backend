package com.example.services;

import com.example.entities.Ticket;
import com.example.entities.User;
import com.example.repositories.TicketRepository;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    public Ticket createTicket(Integer userId, Integer adminId, Ticket ticket) {

        User user = userRepository.findById(userId).orElseThrow();
        ticket.setUser(user);

        // Link to selected admin if provided
        if (adminId != null) {
            User admin = userRepository.findById(adminId).orElse(null);
            if (admin != null) ticket.setAdmin(admin);
        }

        Ticket saved = ticketRepository.save(ticket);

        emailService.sendTicketCreatedEmail(user.getEmail(), saved.getTicketId(), user.getUserId());
        notificationService.notifyUser(user.getUserId(), "✅ Ticket created. ID: " + saved.getTicketId());

        return saved;
    }

    public List<Ticket> getUserTickets(Integer userId) {
        return ticketRepository.findByUserUserId(userId);
    }
}