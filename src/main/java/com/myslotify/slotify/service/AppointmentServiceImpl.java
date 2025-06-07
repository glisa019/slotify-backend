package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.*;
import com.myslotify.slotify.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  TimeSlotRepository timeSlotRepository,
                                  ServiceRepository serviceRepository,
                                  UserRepository userRepository,
                                  EmployeeRepository employeeRepository,
                                  NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Appointment getAppointment(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAppointmentsBetween(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentTimeBetween(start, end);
    }

    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Employee getCurrentEmployee(Authentication auth) {
        String email = auth.getName();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public Appointment createAppointment(UUID slotId, UUID serviceId, Authentication auth) {
        User customer = getCurrentUser(auth);
        return createAppointmentInternal(slotId, serviceId, customer);
    }

    @Override
    public Appointment createAppointmentForCustomer(UUID slotId, UUID serviceId, UUID customerId, Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        if (!slot.getAvailability().getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Unauthorized to book this slot");
        }
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return createAppointmentInternal(slotId, serviceId, customer);
    }

    private Appointment createAppointmentInternal(UUID slotId, UUID serviceId, User customer) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Time slot not available");
        }

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        long slotMinutes = java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        int slotsNeeded = (int) Math.ceil(service.getDuration().getMinutes() / (double) slotMinutes);

        java.util.List<TimeSlot> allSlots = slot.getAvailability().getTimeSlots();
        allSlots.sort(java.util.Comparator.comparing(TimeSlot::getStartTime));
        int index = allSlots.indexOf(slot);
        if (index < 0 || index + slotsNeeded > allSlots.size()) {
            throw new RuntimeException("Not enough consecutive slots available");
        }

        java.util.List<TimeSlot> toBook = new java.util.ArrayList<>();
        java.time.LocalTime expected = slot.getStartTime();
        for (int i = 0; i < slotsNeeded; i++) {
            TimeSlot s = allSlots.get(index + i);
            if (s.getStatus() != SlotStatus.AVAILABLE || !s.getStartTime().equals(expected)) {
                throw new RuntimeException("Required consecutive slots are not available");
            }
            toBook.add(s);
            expected = s.getEndTime();
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID());
        appointment.setEmployee(slot.getAvailability().getEmployee());
        appointment.setCustomer(customer);
        appointment.setAppointmentTime(LocalDateTime.of(slot.getAvailability().getDate(), slot.getStartTime()));
        appointment.setService(service);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointment = appointmentRepository.save(appointment);

        for (TimeSlot s : toBook) {
            s.setStatus(SlotStatus.SCHEDULED);
            s.setAppointment(appointment);
        }
        timeSlotRepository.saveAll(toBook);

        return appointment;
    }

    @Override
    public void cancelAppointment(UUID appointmentId, Authentication auth) {
        User user = getCurrentUser(auth);
        Appointment appointment = getAppointment(appointmentId);
        if (!appointment.getCustomer().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }
        cancelAppointmentInternal(appointment);
    }

    @Override
    public void cancelAppointmentAsEmployee(UUID appointmentId, Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        Appointment appointment = getAppointment(appointmentId);
        if (!appointment.getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }
        cancelAppointmentInternal(appointment);
    }

    private void cancelAppointmentInternal(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        java.util.List<TimeSlot> slots = timeSlotRepository.findAllByAppointmentAppointmentId(appointment.getAppointmentId());
        for (TimeSlot slot : slots) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setAppointment(null);
        }
        if (!slots.isEmpty()) {
            timeSlotRepository.saveAll(slots);
        }
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
