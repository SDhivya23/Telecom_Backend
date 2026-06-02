package com.example.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "holidays")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer holidayId;

    private LocalDate holidayDate;
    private String description;

    @ManyToOne
    @JoinColumn(name = "engineer_id")
    private Engineer engineer;

    private LocalDate date;
}
