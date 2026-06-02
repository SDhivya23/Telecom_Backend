package com.example.repositories;

import com.example.entities.Ticket;
import com.example.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByUserUserId(Integer userId);

    long countByStatus(TicketStatus status);
    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByStatusAndAdminUserId(TicketStatus status, Integer adminId);

    long countByAdminUserId(Integer adminId);

    long countByStatusAndAdminUserId(TicketStatus status, Integer adminId);




}