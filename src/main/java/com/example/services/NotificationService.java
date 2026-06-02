package com.example.services;

import com.example.entities.Notification;
import com.example.entities.User;
import com.example.enums.NotificationStatus;
import com.example.repositories.NotificationRepository;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void notifyUser(Integer userId, String message) {

        User user = userRepository.findById(userId).orElseThrow();

        System.out.println("Saving notification for user: " + userId);

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        if (mailSender != null) {
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(user.getEmail());
                mail.setSubject("Telecom Update");
                mail.setText(message);
                mailSender.send(mail);
            } catch (Exception e) {
                System.out.println("Email failed");
            }
        }

        System.out.println("Notification saved successfully ✅");
    }
}
