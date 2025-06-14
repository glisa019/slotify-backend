package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateUserRequest;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/employee")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getEmployee(id));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping("/employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createEmployee(request));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/employee/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        userService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/user")
    public ResponseEntity<AuthResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
