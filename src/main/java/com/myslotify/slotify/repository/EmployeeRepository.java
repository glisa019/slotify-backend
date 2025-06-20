package com.myslotify.slotify.repository;

import com.myslotify.slotify.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    @Query(value = "SELECT e.* FROM employee e LEFT JOIN \"user\" u ON e.user_id = u.user_id WHERE u.email = :email", nativeQuery = true)
    Optional<Employee> findByUserEmail(@Param("email") String email);
}
