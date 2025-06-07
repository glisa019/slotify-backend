package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.CreateAvailabilityRequest;
import com.myslotify.slotify.entity.EmployeeAvailability;
import com.myslotify.slotify.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeAvailability>> getAvailability(Authentication auth) {
        return ResponseEntity.ok(availabilityService.getAvailabilityForEmployee(auth));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeAvailability>> createAvailability(
            @RequestBody CreateAvailabilityRequest request,
            Authentication auth) {

        List<EmployeeAvailability> created = availabilityService.createAvailabilityForDates(request, auth);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{availabilityId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID availabilityId, Authentication auth) {
        availabilityService.deleteAvailability(availabilityId, auth);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/block/{slotId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> blockTimeSlot(@PathVariable UUID slotId, Authentication auth) {
        availabilityService.blockTimeSlot(slotId, auth);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/unblock/{slotId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> unblockTimeSlot(@PathVariable UUID slotId, Authentication auth) {
        availabilityService.unblockTimeSlot(slotId, auth);
        return ResponseEntity.noContent().build();
    }
}
