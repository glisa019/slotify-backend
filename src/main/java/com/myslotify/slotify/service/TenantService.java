package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.TenantRequest;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.dto.TenantResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TenantService {

    TenantResponse createTenant(TenantRequest request);

    List<Tenant> getAllTenants();

    Tenant getTenantById(UUID id);

    void deleteTenant(UUID id);
}
