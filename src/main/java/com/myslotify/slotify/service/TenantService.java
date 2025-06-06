package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.Tenant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TenantService {

    Tenant createTenant(String name, String schemaName);

    List<Tenant> getAllTenants();

    Tenant getTenantById(UUID id);

    void deleteTenant(UUID id);
}
