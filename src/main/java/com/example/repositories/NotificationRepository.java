package com.example.repositories;

import com.example.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Integer userId);
}