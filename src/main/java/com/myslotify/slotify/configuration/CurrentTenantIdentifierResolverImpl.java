package com.myslotify.slotify.configuration;

import com.myslotify.slotify.util.TenantContext;
import org.hibernate.HibernateException;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        // Retrieve tenant identifier from the context (e.g., a ThreadLocal, HTTP header, etc.)
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new HibernateException("Tenant identifier not found");
        }
        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
