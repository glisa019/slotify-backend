package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.*;
import com.myslotify.slotify.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private TimeSlotRepository timeSlotRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Authentication auth;
    private User user;
    private Employee employee;
    private TimeSlot slot1;
    private TimeSlot slot2;
    private Service service;

    @BeforeEach
    void setUp() {
        auth = new TestingAuthenticationToken("user@example.com", null);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");

        employee = new Employee();
        employee.setEmployeeId(UUID.randomUUID());
        User empUser = new User();
        empUser.setId(UUID.randomUUID());
        employee.setUser(empUser);

        EmployeeAvailability availability = new EmployeeAvailability();
        availability.setEmployee(employee);
        availability.setDate(LocalDate.now());

        slot1 = new TimeSlot();
        slot1.setSlotId(UUID.randomUUID());
        slot1.setStartTime(LocalTime.of(9,0));
        slot1.setEndTime(LocalTime.of(9,15));
        slot1.setAvailability(availability);
        slot1.setStatus(SlotStatus.AVAILABLE);

        slot2 = new TimeSlot();
        slot2.setSlotId(UUID.randomUUID());
        slot2.setStartTime(LocalTime.of(9,15));
        slot2.setEndTime(LocalTime.of(9,30));
        slot2.setAvailability(availability);
        slot2.setStatus(SlotStatus.AVAILABLE);

        java.util.List<TimeSlot> slots = java.util.Arrays.asList(slot1, slot2);
        availability.setTimeSlots(slots);

        service = new Service();
        service.setServiceId(UUID.randomUUID());
        service.setDuration(Interval.MINUTES_30);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(timeSlotRepository.findById(slot1.getSlotId())).thenReturn(Optional.of(slot1));
        when(serviceRepository.findById(service.getServiceId())).thenReturn(Optional.of(service));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void createAppointmentSchedulesSlot() {
        Appointment appt = appointmentService.createAppointment(slot1.getSlotId(), service.getServiceId(), auth);

        assertNotNull(appt);
        assertEquals(AppointmentStatus.SCHEDULED, appt.getStatus());
        assertEquals(SlotStatus.SCHEDULED, slot1.getStatus());
        assertEquals(SlotStatus.SCHEDULED, slot2.getStatus());
        verify(timeSlotRepository).saveAll(any());
    }

    @Test
    void cancelAppointmentMarksSlotAvailable() {
        Appointment appt = appointmentService.createAppointment(slot1.getSlotId(), service.getServiceId(), auth);
        when(appointmentRepository.findById(appt.getAppointmentId())).thenReturn(Optional.of(appt));
        when(timeSlotRepository.findAllByAppointmentAppointmentId(appt.getAppointmentId())).thenReturn(java.util.Arrays.asList(slot1, slot2));

        appointmentService.cancelAppointment(appt.getAppointmentId(), auth);

        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus());
        assertEquals(SlotStatus.AVAILABLE, slot1.getStatus());
        assertEquals(SlotStatus.AVAILABLE, slot2.getStatus());
        verify(timeSlotRepository, times(2)).saveAll(any());
    }

    @Test
    void sendRemindersMarksAppointments() {
        Tenant tenant = new Tenant();
        tenant.setSchemaName("tenant1");
        when(tenantRepository.findAll()).thenReturn(java.util.Collections.singletonList(tenant));
        Appointment appt = new Appointment();
        appt.setAppointmentTime(java.time.LocalDateTime.now().plusHours(1));
        appt.setService(service);
        appt.setCustomer(user);
        appt.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findByAppointmentTimeBetweenAndReminderSentFalse(any(), any()))
                .thenReturn(java.util.Collections.singletonList(appt));

        appointmentService.sendRemindersForUpcomingAppointments();

        assertTrue(appt.isReminderSent());
        verify(notificationService).sendAppointmentReminder(appt);
        verify(appointmentRepository).saveAll(any());
    }
}
