package com.example.entities;

import com.example.enums.EngineerStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "engineers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Engineer {

    @Id
    @Column(name = "engineer_id")
    private Integer engineerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "engineer_id")
    private User user;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "skill_set", length = 255)
    private String skillSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EngineerStatus status;

    @Column(name = "home_lat")
    private Double homeLat;

    @Column(name = "home_lng")
    private Double homeLng;
}