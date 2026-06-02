package com.example.services;

import com.example.entities.*;
import com.example.enums.*;
import com.example.repositories.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EngineerServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private TaskUpdateRepository taskUpdateRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private EngineerRepository engineerRepository;

    @InjectMocks
    private EngineerService engineerService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        when(assignmentService.calculateActiveWorkload(anyInt()))
                .thenReturn(0);
    }

    private Assignment mockAssignment() {

        Engineer engineer = new Engineer();
        engineer.setEngineerId(10);

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@gmail.com");

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setTicketId(100);  // ✅ ADD THIS

        Assignment assignment = new Assignment();
        assignment.setStatus(AssignmentStatus.ACCEPTED);
        assignment.setTicket(ticket);
        assignment.setEngineer(engineer);

        return assignment;
    }

    @Test
    void testUpdateTaskSuccess() {

        Assignment assignment = mockAssignment();

        when(assignmentRepository.findById(1))
                .thenReturn(Optional.of(assignment));

        when(taskUpdateRepository.findByAssignmentAssignmentId(1))
                .thenReturn(Optional.empty());

        engineerService.updateTask(1, "SUCCESS", "Done", "mail");

        verify(emailService).sendCompletedEmail(anyString(), anyInt());
    }

    @Test
    void testUpdateTaskFailure() {

        Assignment assignment = mockAssignment();

        Ticket ticket = assignment.getTicket();
        ticket.setTicketId(101);  // ✅ ADD THIS (IMPORTANT)

        User admin = new User();
        admin.setUserId(2);
        admin.setEmail("admin@gmail.com");

        assignment.setAssignedBy(admin);

        when(assignmentRepository.findById(1))
                .thenReturn(Optional.of(assignment));

        when(taskUpdateRepository.findByAssignmentAssignmentId(1))
                .thenReturn(Optional.empty());

        engineerService.updateTask(1, "FAILURE", "Fail", "mail");

        verify(emailService).sendFailedEmailToAdmin(anyString(), anyInt(), anyInt());
    }

    @Test
    void testUpdateTaskDeferred() {

        Assignment assignment = mockAssignment();

        Ticket ticket = assignment.getTicket();
        ticket.setTicketId(102);  // ✅ ADD THIS

        User admin = new User();
        admin.setUserId(3);
        admin.setEmail("admin@gmail.com");

        assignment.setAssignedBy(admin);

        when(assignmentRepository.findById(1))
                .thenReturn(Optional.of(assignment));

        when(taskUpdateRepository.findByAssignmentAssignmentId(1))
                .thenReturn(Optional.empty());

        engineerService.updateTask(1, "DEFERRED", "Later", "mail");

        verify(emailService).sendDeferredEmailToAdmin(anyString(), anyInt(), anyInt());
    }

    @Test
    void testUpdateTaskInvalidStatus() {

        Assignment assignment = mockAssignment();

        when(assignmentRepository.findById(1))
                .thenReturn(Optional.of(assignment));

        assertThrows(IllegalArgumentException.class, () ->
                engineerService.updateTask(1, "INVALID", "err", "mail"));
    }

    @Test
    void testUpdateTaskNotAccepted() {

        Assignment assignment = mockAssignment();
        assignment.setStatus(AssignmentStatus.REJECTED);

        when(assignmentRepository.findById(1))
                .thenReturn(Optional.of(assignment));

        assertThrows(RuntimeException.class, () ->
                engineerService.updateTask(1, "SUCCESS", "ok", "mail"));
    }
}
