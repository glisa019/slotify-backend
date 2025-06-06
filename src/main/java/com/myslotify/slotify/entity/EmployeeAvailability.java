package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class EmployeeAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID availabilityId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots;
}
