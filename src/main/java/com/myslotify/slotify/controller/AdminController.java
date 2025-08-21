package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateAdminRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/tenant")
    public ResponseEntity<AuthResponse> createTenantAdmin(@RequestBody CreateUserRequest request) {
        logger.info("Creating tenant admin with email {}", request.getEmail());
        return ResponseEntity.ok(adminService.createTenantAdmin(request));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TENANT_ADMIN') and #id == authentication.principal.id)")
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdmin(@PathVariable UUID id) {
        logger.info("Fetching admin {}", id);
        return ResponseEntity.ok(adminService.getAdmin(id));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TENANT_ADMIN') and #id == authentication.principal.id)")
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable UUID id, @RequestBody UpdateAdminRequest request) {
        logger.info("Updating admin {}", id);
        return ResponseEntity.ok(adminService.updateAdmin(id, request));
    }

}
