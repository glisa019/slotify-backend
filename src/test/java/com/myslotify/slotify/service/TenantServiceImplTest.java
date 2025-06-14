package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.SubscriptionStatus;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.exception.NotFoundException;
import com.myslotify.slotify.repository.TenantRepository;
import liquibase.integration.spring.SpringLiquibase;
import com.myslotify.slotify.util.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceImplTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private SpringLiquibase springLiquibase;
    @Mock
    private StripeService stripeService;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;

    @InjectMocks
    private TenantServiceImpl tenantService;

    @BeforeEach
    void setUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        TenantContext.setCurrentTenant("demo_schema");
    }

    @Test
    void activateTenantUpdatesStatusAndCreatesSchema() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setSchemaName("demo_schema");
        tenant.setSubscriptionStatus(SubscriptionStatus.PENDING);
        when(tenantRepository.findBySchemaName("demo_schema"))
                .thenReturn(Optional.of(tenant));

        Tenant result = tenantService.activateTenant();

        assertEquals(SubscriptionStatus.ACTIVE, result.getSubscriptionStatus());
        verify(tenantRepository).save(tenant);
        verify(statement).execute("CREATE SCHEMA IF NOT EXISTS demo_schema");
        verify(springLiquibase).setDataSource(dataSource);
        verify(springLiquibase).setDefaultSchema("demo_schema");
        verify(springLiquibase).setChangeLog("classpath:db/changelog/db.changelog-tenant.xml");
        verify(springLiquibase).afterPropertiesSet();
    }

    @Test
    void activateTenantThrowsWhenNotFound() {
        when(tenantRepository.findBySchemaName("demo_schema"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> tenantService.activateTenant());
    }
}
