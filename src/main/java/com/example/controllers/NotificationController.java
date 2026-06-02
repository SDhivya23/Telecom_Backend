package com.example.controllers;

import com.example.entities.Notification;
import com.example.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/{userId}")
    public List<Notification> getMyNotifications(@PathVariable Integer userId) {

        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }
}