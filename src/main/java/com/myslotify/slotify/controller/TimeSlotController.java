package com.myslotify.slotify.controller;

import com.myslotify.slotify.entity.TimeSlot;
import com.myslotify.slotify.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/slots")
public class TimeSlotController {

    private static final Logger logger = LoggerFactory.getLogger(TimeSlotController.class);

    private final AvailabilityService availabilityService;

    public TimeSlotController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<TimeSlot>> getAvailableSlotsForEmployee(@PathVariable UUID employeeId) {
        logger.info("Fetching available slots for employee {}", employeeId);
        return ResponseEntity.ok(availabilityService.getAvailableTimeSlotsForEmployee(employeeId));
    }
}
