package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.CreateAvailabilityRequest;
import com.myslotify.slotify.entity.EmployeeAvailability;
import com.myslotify.slotify.entity.TimeSlot;
import com.myslotify.slotify.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityController.class);

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    //@PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeAvailability>> getAvailability(Authentication auth) {
        logger.info("Fetching availability for current employee");
        return ResponseEntity.ok(availabilityService.getAvailabilityForEmployee(auth));
    }

    @PostMapping
    //@PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeAvailability>> createAvailability(
            @RequestBody CreateAvailabilityRequest request,
            Authentication auth) {

        logger.info("Creating availability for dates {}", request.getDates());
        List<EmployeeAvailability> created = availabilityService.createAvailabilityForDates(request, auth);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{availabilityId}")
    //@PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID availabilityId, Authentication auth) {
        logger.info("Deleting availability {}", availabilityId);
        availabilityService.deleteAvailability(availabilityId, auth);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/block/{slotId}")
    //@PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> blockTimeSlot(@PathVariable UUID slotId, Authentication auth) {
        logger.info("Blocking time slot {}", slotId);
        availabilityService.blockTimeSlot(slotId, auth);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/unblock/{slotId}")
    //@PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> unblockTimeSlot(@PathVariable UUID slotId, Authentication auth) {
        logger.info("Unblocking time slot {}", slotId);
        availabilityService.unblockTimeSlot(slotId, auth);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available-slots")
    //@PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            Authentication auth) {
        logger.info("Fetching available time slots for current employee on {}", date);
        return ResponseEntity.ok(availabilityService.getAvailableTimeSlotsForEmployee(auth, date));
    }
}
