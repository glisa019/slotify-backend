package com.myslotify.slotify.repository;

import com.myslotify.slotify.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);
}
