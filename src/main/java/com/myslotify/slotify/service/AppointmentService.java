package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.Appointment;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    Appointment getAppointment(UUID id);
    List<Appointment> getAppointments(LocalDate date, Authentication auth);
    void sendRemindersForUpcomingAppointments();

    Appointment createAppointment(UUID slotId, UUID serviceId, Authentication auth);
    Appointment createAppointmentForCustomer(UUID slotId, UUID serviceId, UUID customerId, Authentication auth);
    void cancelAppointment(UUID appointmentId, Authentication auth);
    void cancelAppointmentAsEmployee(UUID appointmentId, Authentication auth);
}
