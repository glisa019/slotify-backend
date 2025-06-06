package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID slotId;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status;

    @ManyToOne
    @JoinColumn(nullable = false)
    private EmployeeAvailability availability;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
}
