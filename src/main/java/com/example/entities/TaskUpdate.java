package com.example.entities;

import com.example.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer updateId;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String remarks;
    private LocalDateTime updatedAt;

    @PrePersist
    public void init() {
        updatedAt = LocalDateTime.now();
    }
}
