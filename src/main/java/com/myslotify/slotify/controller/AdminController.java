package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/tenant")
    public ResponseEntity<Admin> createTenantAdmin(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createTenantAdmin(request));
    }
}
