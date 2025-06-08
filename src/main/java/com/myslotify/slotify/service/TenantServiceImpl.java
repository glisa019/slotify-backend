package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.TenantRequest;
import com.myslotify.slotify.dto.TenantResponse;
import com.myslotify.slotify.entity.SubscriptionStatus;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.exception.BadRequestException;
import com.myslotify.slotify.exception.NotFoundException;
import com.myslotify.slotify.repository.TenantRepository;
import jakarta.transaction.Transactional;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class TenantServiceImpl implements TenantService {

    private final DataSource dataSource;
    private final TenantRepository tenantRepository;
    private final SpringLiquibase springLiquibase;
    private final StripeService stripeService;

    private static final Pattern SCHEMA_PATTERN = Pattern.compile("[A-Za-z0-9_]+");

    public TenantServiceImpl(DataSource dataSource,
                             TenantRepository tenantRepository,
                             SpringLiquibase springLiquibase,
                             StripeService stripeService) {
        this.dataSource = dataSource;
        this.tenantRepository = tenantRepository;
        this.springLiquibase = springLiquibase;
        this.stripeService = stripeService;
    }

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Override
    public TenantResponse createTenant(TenantRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;

        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setSchemaName(request.getSchemaName());
        tenant.setDescription(request.getDescription());
        tenant.setMotto(request.getMotto());
        tenant.setAddress(request.getAddress());
        tenant.setPhone(request.getPhone());
        tenant.setTiktok(request.getTiktok());
        tenant.setInstagram(request.getInstagram());
        tenant.setTextColour(request.getTextColour());
        tenant.setBackgroundColour(request.getBackgroundColour());
        tenant.setBorderColour(request.getBorderColour());
        tenant.setFont(request.getFont());
        tenant.setLogo(request.getLogo());
        tenant.setCoverPicture(request.getCoverPicture());
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setSubscriptionStatus(SubscriptionStatus.PENDING);

        tenantRepository.save(tenant);

        String sessionUrl = null;
        try {
            if (email != null) {
                sessionUrl = stripeService.createSubscriptionSession(email, successUrl, cancelUrl);
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to create Stripe session", e);
        }

        return new TenantResponse(tenant, sessionUrl);
    }

    @Override
    public TenantResponse getCurrentTenant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException("Missing authentication");
        }

        String email = authentication.getName();
        Tenant tenant = tenantRepository.findByTenantAdminEmail(email)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        if (tenant.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
            return new TenantResponse(tenant, null);
        }

        if (tenant.getTenantAdmin() == null) {
            throw new BadRequestException("Tenant admin not set");
        }

        String sessionUrl;
        try {
            sessionUrl = stripeService.createSubscriptionSession(
                    tenant.getTenantAdmin().getEmail(), successUrl, cancelUrl);
        } catch (Exception e) {
            throw new BadRequestException("Failed to create Stripe session", e);
        }

        return new TenantResponse(tenant, sessionUrl);
    }

    @Transactional
    public Tenant activateTenant(String name, String schemaName) {
        Tenant tenant = tenantRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        tenant.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        tenantRepository.save(tenant);

        createSchema(schemaName);

        applyLiquibaseChangeSets(schemaName);

        return tenant;
    }

    private void createSchema(String schemaName) {
        if (!SCHEMA_PATTERN.matcher(schemaName).matches()) {
            throw new IllegalArgumentException("Invalid schema name");
        }
        try (Connection connection = dataSource.getConnection()) {
            String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
            connection.createStatement().execute(createSchemaSql);
        } catch (SQLException e) {
            throw new BadRequestException("Failed to create schema: " + schemaName, e);
        }
    }

    private void applyLiquibaseChangeSets(String schemaName) {
        try {
            springLiquibase.setDataSource(dataSource);
            springLiquibase.setDefaultSchema(schemaName);
            springLiquibase.setChangeLog("classpath:db/changelog/db.changelog-tenant.xml");
            springLiquibase.afterPropertiesSet();
        } catch (Exception e) {
            throw new BadRequestException("Failed to apply Liquibase changes for schema: " + schemaName, e);
        }
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Tenant getTenantById(UUID id) {
        return tenantRepository.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found"));
    }

    @Override
    public void deleteTenant(UUID id) {
        tenantRepository.deleteById(id);
    }
}

