package com.myslotify.slotify.controller;

import com.myslotify.slotify.entity.Appointment;
import com.myslotify.slotify.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointmentsBetween(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getAppointmentsBetween(start, end));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appointment> createAppointment(
            @RequestParam UUID slotId,
            @RequestParam UUID serviceId,
            Authentication auth) {
        return ResponseEntity.ok(appointmentService.createAppointment(slotId, serviceId, auth));
    }

    @PostMapping("/customer")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Appointment> createAppointmentForCustomer(
            @RequestParam UUID slotId,
            @RequestParam UUID serviceId,
            @RequestParam UUID customerId,
            Authentication auth) {
        return ResponseEntity.ok(
                appointmentService.createAppointmentForCustomer(slotId, serviceId, customerId, auth));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancelAppointment(@PathVariable UUID id, Authentication auth) {
        appointmentService.cancelAppointment(id, auth);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> cancelAppointmentAsEmployee(@PathVariable UUID id, Authentication auth) {
        appointmentService.cancelAppointmentAsEmployee(id, auth);
        return ResponseEntity.noContent().build();
    }
}
