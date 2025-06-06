package com.myslotify.slotify.repository;

import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.EmployeeAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeAvailabilityRepository extends JpaRepository<EmployeeAvailability, UUID> {

    List<EmployeeAvailability> findByEmployeeEmployeeId(UUID employeeId);

    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
}
