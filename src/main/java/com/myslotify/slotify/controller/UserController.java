package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateUserRequest;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('CUSTOMER')")
    @GetMapping("/employee")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Fetching all employees");
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or (hasRole('EMPLOYEE') and #id == authentication.principal.id)")
    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable UUID id) {
        logger.info("Fetching employee {}", id);
        return ResponseEntity.ok(userService.getEmployee(id));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee/me")
    public ResponseEntity<Employee> getCurrentEmployee(Authentication auth) {
        logger.info("Fetching current employee");
        return ResponseEntity.ok(userService.getCurrentEmployee(auth));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping("/employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateUserRequest request) {
        logger.info("Creating employee {}", request.getEmail());
        return ResponseEntity.ok(userService.createEmployee(request));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/employee/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        logger.info("Deleting employee {}", id);
        userService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or (hasRole('CUSTOMER') and #id == authentication.principal.id)")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        logger.info("Fetching user {}", id);
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/user/me")
    public ResponseEntity<User> getCurrentUser(Authentication auth) {
        logger.info("Fetching current user");
        return ResponseEntity.ok(userService.getCurrentUser(auth));
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/user")
    public ResponseEntity<AuthResponse> createUser(@RequestBody CreateUserRequest request) {
        logger.info("Creating user {}", request.getEmail());
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        logger.info("Updating user {}", id);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        logger.info("Deleting user {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
