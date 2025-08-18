package com.myslotify.slotify.controller;

import com.myslotify.slotify.entity.Appointment;
import com.myslotify.slotify.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable UUID id) {
        logger.info("Fetching appointment {}", id);
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','EMPLOYEE')")
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication auth) {
        logger.info("Fetching appointments for date {}", date);
        return ResponseEntity.ok(appointmentService.getAppointments(date, auth));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appointment> createAppointment(
            @RequestParam UUID slotId,
            @RequestParam UUID serviceId,
            Authentication auth) {
        logger.info("Creating appointment for slot {} and service {}", slotId, serviceId);
        return ResponseEntity.ok(appointmentService.createAppointment(slotId, serviceId, auth));
    }

    @PostMapping("/customer")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Appointment> createAppointmentForCustomer(
            @RequestParam UUID slotId,
            @RequestParam UUID serviceId,
            @RequestParam UUID customerId,
            Authentication auth) {
        logger.info("Employee creating appointment for customer {} on slot {} and service {}", customerId, slotId, serviceId);
        return ResponseEntity.ok(
                appointmentService.createAppointmentForCustomer(slotId, serviceId, customerId, auth));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancelAppointment(@PathVariable UUID id, Authentication auth) {
        logger.info("Cancelling appointment {} as customer", id);
        appointmentService.cancelAppointment(id, auth);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> cancelAppointmentAsEmployee(@PathVariable UUID id, Authentication auth) {
        logger.info("Cancelling appointment {} as employee", id);
        appointmentService.cancelAppointmentAsEmployee(id, auth);
        return ResponseEntity.noContent().build();
    }
}
