package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.AdminRole;
import com.myslotify.slotify.entity.Role;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.entity.SubscriptionStatus;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.exception.UnauthorizedException;
import com.myslotify.slotify.repository.AdminRepository;
import com.myslotify.slotify.repository.UserRepository;
import com.myslotify.slotify.repository.TenantRepository;
import com.myslotify.slotify.util.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        TenantContext.clear();
    }

    @Test
    void loginAdminSuccess() {
        LoginRequest request = new LoginRequest();
        request.email = "admin@example.com";
        request.password = "pass";

        Admin admin = new Admin();
        admin.setPassword("hashed");
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        when(jwtService.generateToken(admin)).thenReturn("token");

        AuthResponse response = authService.login(request);
        assertEquals("token", response.getToken());
        assertEquals(admin, response.getAccount());
    }

    @Test
    void loginUserSuccess() {
        TenantContext.setCurrentTenant("tenant1");
        LoginRequest request = new LoginRequest();
        request.email = "user@example.com";
        request.password = "pass";

        Tenant tenant = new Tenant();
        tenant.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        when(tenantRepository.findBySchemaName("tenant1")).thenReturn(Optional.of(tenant));

        User user = new User();
        user.setPassword("hash");
        user.setRole(Role.EMPLOYEE);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token");

        AuthResponse response = authService.login(request);
        assertEquals("token", response.getToken());
        assertEquals(user, response.getAccount());
    }

    @Test
    void loginFailsWhenTenantInactive() {
        TenantContext.setCurrentTenant("tenant1");
        LoginRequest request = new LoginRequest();
        request.email = "user@example.com";
        request.password = "pass";

        Tenant tenant = new Tenant();
        tenant.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
        when(tenantRepository.findBySchemaName("tenant1")).thenReturn(Optional.of(tenant));

        User user = new User();
        user.setPassword("hash");
        user.setRole(Role.EMPLOYEE);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void loginTenantAdminFailsWhenSubscriptionPending() {
        LoginRequest request = new LoginRequest();
        request.email = "admin@example.com";
        request.password = "pass";

        Admin admin = new Admin();
        admin.setPassword("hash");
        admin.setRole(AdminRole.TENANT_ADMIN);
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        Tenant tenant = new Tenant();
        tenant.setSubscriptionStatus(SubscriptionStatus.PENDING);
        when(tenantRepository.findByTenantAdminEmail("admin@example.com")).thenReturn(Optional.of(tenant));

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
