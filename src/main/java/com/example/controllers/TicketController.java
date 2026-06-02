package com.example.controllers;

import com.example.entities.Ticket;
import com.example.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/api/user/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/{userId}")
    public Ticket create(@PathVariable Integer userId,
                         @RequestParam(required = false) Integer adminId,
                         @RequestBody Ticket ticket) {
        return ticketService.createTicket(userId, adminId, ticket);
    }

    @GetMapping("/{userId}")
    public List<Ticket> get(@PathVariable Integer userId) {
        return ticketService.getUserTickets(userId);
    }
}
