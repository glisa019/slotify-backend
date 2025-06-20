package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.*;
import com.myslotify.slotify.exception.BadRequestException;
import com.myslotify.slotify.exception.NotFoundException;
import com.myslotify.slotify.exception.UnauthorizedException;
import com.myslotify.slotify.repository.*;
import com.myslotify.slotify.util.TenantContext;
import com.myslotify.slotify.util.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final TenantRepository tenantRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  TimeSlotRepository timeSlotRepository,
                                  ServiceRepository serviceRepository,
                                  UserRepository userRepository,
                                  EmployeeRepository employeeRepository,
                                  NotificationService notificationService,
                                  TenantRepository tenantRepository) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Appointment getAppointment(UUID id) {
        logger.info("Fetching appointment {}", id);
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAppointmentsBetween(LocalDateTime start, LocalDateTime end) {
        logger.info("Fetching appointments between {} and {}", start, end);
        return appointmentRepository.findByAppointmentTimeBetween(start, end);
    }

    private User getCurrentUser(Authentication auth) {
        String email = SecurityUtil.extractEmail(auth);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Employee getCurrentEmployee(Authentication auth) {
        String email = SecurityUtil.extractEmail(auth);
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    @Override
    public Appointment createAppointment(UUID slotId, UUID serviceId, Authentication auth) {
        logger.info("Creating appointment for slot {} and service {}", slotId, serviceId);
        User customer = getCurrentUser(auth);
        return createAppointmentInternal(slotId, serviceId, customer);
    }

    @Override
    public Appointment createAppointmentForCustomer(UUID slotId, UUID serviceId, UUID customerId, Authentication auth) {
        logger.info("Employee creating appointment for customer {} on slot {} service {}", customerId, slotId, serviceId);
        Employee employee = getCurrentEmployee(auth);
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Time slot not found"));
        if (!slot.getAvailability().getEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedException("Unauthorized to book this slot");
        }
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        return createAppointmentInternal(slotId, serviceId, customer);
    }

    private Appointment createAppointmentInternal(UUID slotId, UUID serviceId, User customer) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Time slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new BadRequestException("Time slot not available");
        }

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        long slotMinutes = java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        int slotsNeeded = (int) Math.ceil(service.getDuration().getMinutes() / (double) slotMinutes);

        java.util.List<TimeSlot> allSlots = slot.getAvailability().getTimeSlots();
        allSlots.sort(java.util.Comparator.comparing(TimeSlot::getStartTime));
        int index = allSlots.indexOf(slot);
        if (index < 0 || index + slotsNeeded > allSlots.size()) {
            throw new BadRequestException("Not enough consecutive slots available");
        }

        java.util.List<TimeSlot> toBook = new java.util.ArrayList<>();
        java.time.LocalTime expected = slot.getStartTime();
        for (int i = 0; i < slotsNeeded; i++) {
            TimeSlot s = allSlots.get(index + i);
            if (s.getStatus() != SlotStatus.AVAILABLE || !s.getStartTime().equals(expected)) {
                throw new BadRequestException("Required consecutive slots are not available");
            }
            toBook.add(s);
            expected = s.getEndTime();
        }

        Appointment appointment = new Appointment();
        appointment.setEmployee(slot.getAvailability().getEmployee());
        appointment.setCustomer(customer);
        appointment.setAppointmentTime(LocalDateTime.of(slot.getAvailability().getDate(), slot.getStartTime()));
        appointment.setService(service);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReminderSent(false);

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
        logger.info("Cancelling appointment {} as customer", appointmentId);
        User user = getCurrentUser(auth);
        Appointment appointment = getAppointment(appointmentId);
        if (!appointment.getCustomer().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized to cancel this appointment");
        }
        cancelAppointmentInternal(appointment);
    }

    @Override
    public void cancelAppointmentAsEmployee(UUID appointmentId, Authentication auth) {
        logger.info("Cancelling appointment {} as employee", appointmentId);
        Employee employee = getCurrentEmployee(auth);
        Appointment appointment = getAppointment(appointmentId);
        if (!appointment.getEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedException("Unauthorized to cancel this appointment");
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

    // Runs hourly to send reminders for appointments in the next 24 hours for each tenant
    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void sendRemindersForUpcomingAppointments() {
        logger.info("Sending appointment reminders for all tenants");
        List<Tenant> tenants = tenantRepository.findAllBySubscriptionStatus(SubscriptionStatus.ACTIVE);
        for (Tenant tenant : tenants) {
            try {
                logger.info("Processing reminders for tenant {}", tenant.getName());
                TenantContext.setCurrentTenant(tenant.getSchemaName());
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime tomorrow = now.plusHours(24);
                List<Appointment> upcoming = appointmentRepository
                        .findByAppointmentTimeBetweenAndReminderSentFalse(now, tomorrow);
                for (Appointment appt : upcoming) {
                    notificationService.sendAppointmentReminder(appt);
                    appt.setReminderSent(true);
                }
                if (!upcoming.isEmpty()) {
                    appointmentRepository.saveAll(upcoming);
                }
            } finally {
                TenantContext.clear();
            }
        }
    }
}
