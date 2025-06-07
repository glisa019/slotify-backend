package com.myslotify.slotify.repository;

import com.myslotify.slotify.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    TimeSlot findByAppointmentAppointmentId(UUID appointmentId);

    java.util.List<TimeSlot> findAllByAppointmentAppointmentId(UUID appointmentId);
}
