package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.Appointment;
import com.myslotify.slotify.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Appointment getAppointment(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAppointmentsBetween(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentTimeBetween(start, end);
    }

    // Runs hourly to send reminders for appointments in the next 24 hours
    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void sendRemindersForUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(24);
        List<Appointment> upcoming = getAppointmentsBetween(now, tomorrow);
        for (Appointment appt : upcoming) {
            notificationService.sendAppointmentReminder(appt);
        }
    }
}
