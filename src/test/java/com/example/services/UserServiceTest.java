package com.example.services;

import com.example.entities.User;
import com.example.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateProfile() {

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@gmail.com");
        user.setName("Dhivya");

        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any())).thenReturn(user);

        User updated = userService.updateProfile("user@gmail.com", new User());

        assertNotNull(updated);

        verify(emailService, times(1))
                .sendProfileUpdateEmail(anyString(), anyString(), anyInt());
    }
}