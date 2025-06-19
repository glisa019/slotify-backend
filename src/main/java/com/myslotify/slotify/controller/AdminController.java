package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
