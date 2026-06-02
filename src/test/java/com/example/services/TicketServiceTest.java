package com.example.services;

import com.example.entities.Ticket;
import com.example.entities.User;
import com.example.repositories.TicketRepository;
import com.example.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTicket() {

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@gmail.com");


        Ticket ticket = new Ticket();
        ticket.setTicketId(100);

        when(userRepository.findById(1))
                .thenReturn(java.util.Optional.of(user));

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        Ticket result = ticketService.createTicket(1, ticket);

        assertNotNull(result);

        verify(emailService, times(1))
                .sendTicketCreatedEmail(anyString(), anyInt(), anyInt());

        verify(notificationService, times(1))
                .notifyUser(anyInt(), anyString());
    }
}