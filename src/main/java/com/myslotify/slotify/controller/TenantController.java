package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.TenantRequest;
import com.myslotify.slotify.dto.TenantResponse;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@RequestBody TenantRequest request) {
        return ResponseEntity.ok(tenantService.createTenant(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
