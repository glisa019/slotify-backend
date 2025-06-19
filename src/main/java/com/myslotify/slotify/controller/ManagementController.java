package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.CreateServiceRequest;
import com.myslotify.slotify.dto.UpdateServiceRequest;
import com.myslotify.slotify.entity.Service;
import com.myslotify.slotify.service.ManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/manage")
public class ManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ManagementController.class);

    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping("/services")
    public ResponseEntity<Service> createService(@RequestBody CreateServiceRequest request) {
        logger.info("Creating service {}", request.getName());
        return ResponseEntity.ok(managementService.createService(request));
    }

    @GetMapping("/services")
    public ResponseEntity<List<Service>> getAllServices() {
        logger.info("Fetching all services");
        return ResponseEntity.ok(managementService.getAllServices());
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<Service> getService(@PathVariable UUID id) {
        logger.info("Fetching service {}", id);
        return ResponseEntity.ok(managementService.getService(id));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PutMapping("/services/{id}")
    public ResponseEntity<Service> updateService(@PathVariable UUID id, @RequestBody UpdateServiceRequest request) {
        logger.info("Updating service {}", id);
        return ResponseEntity.ok(managementService.updateService(id, request));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        logger.info("Deleting service {}", id);
        managementService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
