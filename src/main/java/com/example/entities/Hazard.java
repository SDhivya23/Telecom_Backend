package com.example.entities;

import com.example.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hazards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hazard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hazardId;

    private String location;
    private Double latitude;
    private Double longitude;
    private String description;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;

    @PrePersist
    public void init() {
        createdAt = LocalDateTime.now();
    }
}