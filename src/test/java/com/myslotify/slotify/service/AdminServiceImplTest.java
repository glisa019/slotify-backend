package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.AdminRole;
import com.myslotify.slotify.exception.BadRequestException;
import com.myslotify.slotify.repository.AdminRepository;
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
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void createsTenantAdmin() {
        CreateUserRequest request = new CreateUserRequest();
        request.email = "admin@example.com";
        request.password = "pass";
        request.firstName = "A";
        request.lastName = "B";
        request.phone = "123";

        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hash");
        when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> inv.getArgument(0));

        Admin admin = adminService.createTenantAdmin(request);
        assertEquals(AdminRole.TENANT_ADMIN, admin.getRole());
        assertEquals("hash", admin.getPassword());
    }

    @Test
    void throwsWhenAdminExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.email = "admin@example.com";

        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(new Admin()));

        assertThrows(BadRequestException.class, () -> adminService.createTenantAdmin(request));
    }
}
