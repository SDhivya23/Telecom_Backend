package com.example.services;

import com.example.entities.*;
import com.example.repositories.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private EngineerRepository engineerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HazardRepository hazardRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AssignmentService assignmentService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        mockSecurityContext();
    }

    @Test
    void testAssignWithHazard() {

        Ticket ticket = new Ticket();
        ticket.setTicketId(1);
        ticket.setLocation("Whitefield");

        Engineer engineer = new Engineer();
        engineer.setEngineerId(10);
        User engUser = new User();
        engUser.setEmail("engineer@gmail.com");
        engineer.setUser(engUser);

        User admin = new User();
        admin.setEmail("admin@gmail.com");

        Hazard hazard = Hazard.builder()
                .location("Whitefield")
                .description("Cable cut")
                .riskLevel(com.example.enums.RiskLevel.HIGH)
                .build();

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(engineerRepository.findById(10)).thenReturn(Optional.of(engineer));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(admin));
        when(hazardRepository.findByLocation("Whitefield")).thenReturn(List.of(hazard));
        when(assignmentRepository.save(any())).thenReturn(new Assignment());

        Assignment result = assignmentService.assign(1, 10);

        assertNotNull(result);

        verify(emailService).sendHazardAwareAssignmentEmail(any(), any(), anyInt(), anyInt(), any());
    }

    private void mockSecurityContext() {
        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        var auth = mock(org.springframework.security.core.Authentication.class);

        when(auth.getName()).thenReturn("admin@gmail.com");
        when(context.getAuthentication()).thenReturn(auth);

        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
    }
}