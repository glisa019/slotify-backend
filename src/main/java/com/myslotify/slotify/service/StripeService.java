package com.myslotify.slotify.service;

import org.springframework.stereotype.Service;

@Service
public interface StripeService {
    String createSubscriptionSession(String email, String successUrl, String cancelUrl) throws Exception;

    /**
     * Check if a customer's subscription is active.
     *
     * @param email customer email address registered with Stripe
     * @return true if an active subscription exists
     */
    boolean isSubscriptionActive(String email) throws Exception;
}
