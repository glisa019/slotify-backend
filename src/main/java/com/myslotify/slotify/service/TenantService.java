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

    Tenant updateTenant(TenantRequest request);

    List<Tenant> getAllTenants();

    Tenant getTenantById(UUID id);

    void deleteTenant(UUID id);

    /**
     * Fetch information for the tenant owned by the currently authenticated
     * TENANT_ADMIN. The tenant is resolved from the admin's user account.
     * If the subscription is pending, a new Stripe Checkout session is created
     * and returned in the response.
     *
     * @return tenant data and optionally a payment URL
     */
    TenantResponse getCurrentTenant();

    /**
     * Activate the currently authenticated tenant after payment confirmation.
     * The schema will be created and Liquibase migrations applied.
     *
     * @return the updated tenant entity
     */
    Tenant activateTenant();
}
