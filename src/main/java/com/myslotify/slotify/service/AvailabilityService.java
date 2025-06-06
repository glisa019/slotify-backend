package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateAvailabilityRequest;
import com.myslotify.slotify.entity.EmployeeAvailability;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface AvailabilityService {

    List<EmployeeAvailability> getAvailabilityForEmployee(Authentication auth);
    List<EmployeeAvailability> createAvailabilityForDates(CreateAvailabilityRequest request, Authentication auth);
    void deleteAvailability(UUID availabilityId, Authentication auth);
    void blockTimeSlot(UUID timeSlotId, Authentication auth);
    void unblockTimeSlot(UUID timeSlotId, Authentication auth);
}
