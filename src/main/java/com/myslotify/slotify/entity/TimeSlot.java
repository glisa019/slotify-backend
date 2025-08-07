package com.myslotify.slotify.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "time_slot")
@Data
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "slot_id")
    private UUID slotId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "availability_id", nullable = false)
    private EmployeeAvailability availability;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
}
