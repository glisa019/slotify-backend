package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateAvailabilityRequest;
import com.myslotify.slotify.entity.*;
import com.myslotify.slotify.repository.EmployeeAvailabilityRepository;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.repository.ServiceRepository;
import com.myslotify.slotify.repository.TimeSlotRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeAvailabilityRepository availabilityRepository;
    @Mock
    private TimeSlotRepository timeSlotRepository;
    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private Authentication auth;
    private Employee employee;
    private User user;

    @BeforeEach
    void setUp() {
        auth = new TestingAuthenticationToken("employee@example.com", null);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("employee@example.com");

        employee = new Employee();
        employee.setEmployeeId(UUID.randomUUID());
        employee.setUser(user);

        when(employeeRepository.findByUserEmail("employee@example.com"))
                .thenReturn(Optional.of(employee));
        when(availabilityRepository.save(any(EmployeeAvailability.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createsSlotsBasedOnServiceDuration() {
        Service service = new Service();
        service.setDuration(Interval.MINUTES_30);
        when(serviceRepository.findAll()).thenReturn(Collections.singletonList(service));
        when(availabilityRepository.existsByEmployeeAndDate(any(), any())).thenReturn(false);

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setDates(Collections.singletonList(LocalDate.now()));
        request.setShiftStart(LocalTime.of(9,0));
        request.setShiftEnd(LocalTime.of(10,0));

        List<EmployeeAvailability> result = availabilityService.createAvailabilityForDates(request, auth);

        assertEquals(1, result.size());
        EmployeeAvailability availability = result.get(0);
        assertNotNull(availability.getTimeSlots());
        assertEquals(2, availability.getTimeSlots().size());

        verify(timeSlotRepository).saveAll(any());
    }
}
