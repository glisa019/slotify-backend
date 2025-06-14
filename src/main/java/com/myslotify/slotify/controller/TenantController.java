package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.TenantRequest;
import com.myslotify.slotify.dto.TenantResponse;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

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

    @PreAuthorize("permitAll()")
    @GetMapping("/key/{key}")
    public ResponseEntity<Tenant> getTenantByKey(@PathVariable String key) {
        return ResponseEntity.ok(tenantService.getTenantByKey(key));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@RequestBody TenantRequest request) {
        return ResponseEntity.ok(tenantService.createTenant(request));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<TenantResponse> getCurrentTenant() {
        return ResponseEntity.ok(tenantService.getCurrentTenant());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PutMapping("/me")
    @Operation(summary = "Update current tenant information",
            description = "Allows a tenant admin to modify the tenant profile")
    public ResponseEntity<Tenant> updateTenant(@RequestBody TenantRequest request) {
        return ResponseEntity.ok(tenantService.updateTenant(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<Tenant> activateTenant(@PathVariable UUID id) {
        Tenant tenant = tenantService.activateTenant(id);
        return ResponseEntity.ok(tenant);
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping("/activate")
    public ResponseEntity<Tenant> activateTenant() {
        Tenant tenant = tenantService.activateTenant();
        return ResponseEntity.ok(tenant);
    }
}
