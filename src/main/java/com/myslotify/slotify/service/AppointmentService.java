package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    Appointment getAppointment(UUID id);
    List<Appointment> getAppointmentsBetween(LocalDateTime start, LocalDateTime end);
    void sendRemindersForUpcomingAppointments();
}
