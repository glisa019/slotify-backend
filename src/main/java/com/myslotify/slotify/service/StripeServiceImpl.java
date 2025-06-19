package com.myslotify.slotify.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Value("${stripe.api.secret}")
    private String stripeSecretKey;

    @Value("${stripe.price.id}")
    private String priceId;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public String createSubscriptionSession(String email, String successUrl, String cancelUrl) throws Exception {
        logger.info("Creating Stripe subscription session for {}", email);
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomerEmail(email)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice(priceId)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    @Override
    public boolean isSubscriptionActive(String email) throws Exception {
        logger.info("Checking subscription status for {}", email);
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", email);
        customerParams.put("limit", 1L);
        CustomerCollection customers = Customer.list(customerParams);
        if (customers.getData().isEmpty()) {
            return false;
        }

        String customerId = customers.getData().get(0).getId();

        Map<String, Object> subParams = new HashMap<>();
        subParams.put("customer", customerId);
        SubscriptionCollection subscriptions = Subscription.list(subParams);
        for (Subscription sub : subscriptions.getData()) {
            if ("active".equals(sub.getStatus())) {
                return true;
            }
        }
        return false;
    }
}
