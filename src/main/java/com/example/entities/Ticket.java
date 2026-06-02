package com.example.entities;

import com.example.enums.TicketStatus;
import com.example.enums.TicketType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    private String description;
    private String location;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private Integer priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void setDefault() {

        this.status = TicketStatus.OPEN;

        // ✅ PRIORITY LOGIC (FR12)
        if (this.type == TicketType.FAULT) {
            this.priority = 2;
        } else {
            this.priority = 1;
        }

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void update() {
        this.updatedAt = LocalDateTime.now();
    }


}
