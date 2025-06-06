package com.myslotify.slotify.service;

import org.springframework.stereotype.Service;

@Service
public interface StripeService {
    String createSubscriptionSession(String email, String successUrl, String cancelUrl) throws Exception;
}
