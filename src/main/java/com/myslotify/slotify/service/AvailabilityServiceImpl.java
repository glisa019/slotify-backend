package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateAvailabilityRequest;
import com.myslotify.slotify.entity.*;
import com.myslotify.slotify.repository.EmployeeAvailabilityRepository;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.repository.TimeSlotRepository;
import com.myslotify.slotify.repository.ServiceRepository;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@org.springframework.stereotype.Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAvailabilityRepository availabilityRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ServiceRepository serviceRepository;

    public AvailabilityServiceImpl(EmployeeRepository employeeRepository,
                                   EmployeeAvailabilityRepository availabilityRepository,
                                   TimeSlotRepository timeSlotRepository,
                                   ServiceRepository serviceRepository) {
        this.employeeRepository = employeeRepository;
        this.availabilityRepository = availabilityRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.serviceRepository = serviceRepository;
    }

    private Employee getCurrentEmployee(Authentication authentication) {
        String email = authentication.getName();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public List<EmployeeAvailability> getAvailabilityForEmployee(Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        return availabilityRepository.findByEmployeeId(employee.getId());
    }

    public List<EmployeeAvailability> createAvailabilityForDates(CreateAvailabilityRequest request, Authentication auth) {

        Employee employee = getCurrentEmployee(auth);

        List<EmployeeAvailability> availabilities = new ArrayList<>();

        for(LocalDate date : request.getDates()) {

            if (availabilityRepository.existsByEmployeeAndDate(employee, date)) {
                throw new RuntimeException("Availability for this date already exists");
            }

            EmployeeAvailability availability = new EmployeeAvailability();
            availability.setAvailabilityId(UUID.randomUUID());
            availability.setDate(date);
            availability.setEmployee(employee);
            availability = availabilityRepository.save(availability);

            int intervalMinutes = serviceRepository.findAll().stream()
                    .map(Service::getDuration)
                    .mapToInt(Interval::getMinutes)
                    .min()
                    .orElse(Arrays.stream(Interval.values())
                            .mapToInt(Interval::getMinutes)
                            .min()
                            .orElse(15));

            List<TimeSlot> timeSlots = new ArrayList<>();
            LocalTime current = request.getShiftStart();

            while (!current.plusMinutes(intervalMinutes).isAfter(request.getShiftEnd())) {
                TimeSlot slot = new TimeSlot();
                slot.setSlotId(UUID.randomUUID());
                slot.setStartTime(current);
                slot.setEndTime(current.plusMinutes(intervalMinutes));
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setAvailability(availability);

                timeSlots.add(slot);
                current = current.plusMinutes(intervalMinutes);
            }

            timeSlotRepository.saveAll(timeSlots);
            availability.setTimeSlots(timeSlots);
            availabilities.add(availability);
        }

        return availabilities;
    }

    public void deleteAvailability(UUID availabilityId, Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        EmployeeAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        if (!availability.getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Unauthorized to delete this availability");
        }

        timeSlotRepository.deleteAll(availability.getTimeSlots());
        availabilityRepository.delete(availability);
    }

    public void blockTimeSlot(UUID timeSlotId, Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (!slot.getAvailability().getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Unauthorized to block this time slot");
        }

        if (slot.getStatus() == SlotStatus.SCHEDULED) {
            throw new RuntimeException("Cannot block a scheduled time slot");
        }

        slot.setStatus(SlotStatus.BLOCKED);
        slot.setAppointment(null);
        timeSlotRepository.save(slot);
    }

    public void unblockTimeSlot(UUID timeSlotId, Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (!slot.getAvailability().getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Unauthorized to unblock this time slot");
        }

        if (slot.getStatus() != SlotStatus.BLOCKED) {
            throw new RuntimeException("Only blocked time slots can be unblocked");
        }

        slot.setStatus(SlotStatus.AVAILABLE);
        timeSlotRepository.save(slot);
    }

    public List<TimeSlot> getAvailableTimeSlotsForEmployee(Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        return timeSlotRepository
                .findAllByAvailabilityEmployeeEmployeeIdAndStatus(employee.getId(), SlotStatus.AVAILABLE);
    }

    public List<TimeSlot> getAvailableTimeSlotsForEmployee(UUID employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new RuntimeException("Employee not found");
        }
        return timeSlotRepository
                .findAllByAvailabilityEmployeeEmployeeIdAndStatus(employeeId, SlotStatus.AVAILABLE);
    }
}
