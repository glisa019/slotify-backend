package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.SubscriptionStatus;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.repository.TenantRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionChecker {

    private final TenantRepository tenantRepository;
    private final StripeService stripeService;

    public SubscriptionChecker(TenantRepository tenantRepository, StripeService stripeService) {
        this.tenantRepository = tenantRepository;
        this.stripeService = stripeService;
    }

    /**
     * Daily job to verify tenant subscriptions with Stripe.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void checkSubscriptions() {
        List<Tenant> tenants = tenantRepository.findAll();
        for (Tenant tenant : tenants) {
            try {
                if (tenant.getTenantAdmin() == null) {
                    continue;
                }
                boolean active = stripeService.isSubscriptionActive(tenant.getTenantAdmin().getEmail());
                if (!active && tenant.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
                    tenant.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
                    tenantRepository.save(tenant);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
