package com.myslotify.slotify.filter;

import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.AdminRole;
import com.myslotify.slotify.repository.AdminRepository;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.repository.UserRepository;
import com.myslotify.slotify.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesTenantAdminWithTenantHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        request.addHeader("X-Tenant-ID", "tenant1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Admin admin = new Admin();
        admin.setRole(AdminRole.TENANT_ADMIN);

        when(jwtService.extractEmail("token")).thenReturn("admin@example.com");
        when(jwtService.extractRole("token")).thenReturn("TENANT_ADMIN");
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(jwtService.isTokenValid("token", admin)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication should be set");
        assertEquals(admin, auth.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }
}
