package com.example.services;

import com.example.entities.Hazard;
import com.example.entities.User;
import com.example.repositories.HazardRepository;
import com.example.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class HazardServiceTest {

    @Mock
    private HazardRepository hazardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HazardService hazardService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        mockSecurityContext();
    }

    @Test
    void testSaveHazard() {

        Hazard hazard = new Hazard();
        hazard.setLocation("Whitefield");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(new User()));

        when(hazardRepository.save(any())).thenReturn(hazard);

        Hazard saved = hazardService.save(hazard);

        assertNotNull(saved);
        verify(hazardRepository).save(any());
    }

    private void mockSecurityContext() {
        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        var auth = mock(org.springframework.security.core.Authentication.class);

        when(auth.getName()).thenReturn("admin@gmail.com");
        when(context.getAuthentication()).thenReturn(auth);

        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
    }
}