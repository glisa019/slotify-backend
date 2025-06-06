package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.TenantRequest;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.SubscriptionStatus;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.repository.TenantRepository;
import jakarta.transaction.Transactional;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private SpringLiquibase springLiquibase;
    @Autowired
    private StripeService stripeService;

    public Tenant createTenant(TenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setSchemaName(request.getSchemaName());
        tenant.setDescription(request.getDescription());
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setSubscriptionStatus(SubscriptionStatus.PENDING);

        tenantRepository.save(tenant);

        return tenant;
    }

    @Transactional
    public Tenant activateTenant(String name, String schemaName) {
        Tenant tenant = tenantRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        tenantRepository.save(tenant);

        createSchema(schemaName);

        applyLiquibaseChangeSets(schemaName);

        return tenant;
    }

    private void createSchema(String schemaName) {
        try (Connection connection = dataSource.getConnection()) {
            String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
            connection.createStatement().execute(createSchemaSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema: " + schemaName, e);
        }
    }

    private void applyLiquibaseChangeSets(String schemaName) {
        try {
            springLiquibase.setDataSource(dataSource);
            springLiquibase.setDefaultSchema(schemaName);
            springLiquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
            springLiquibase.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply Liquibase changes for schema: " + schemaName, e);
        }
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenantById(UUID id) {
        return tenantRepository.findById(id).orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    public void deleteTenant(UUID id) {
        tenantRepository.deleteById(id);
    }
}

