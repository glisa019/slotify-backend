package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.Appointment;

public interface NotificationService {
    void sendEmail(String to, String subject, String body);
    void sendSms(String to, String message);
    void sendAppointmentReminder(Appointment appointment);
}
