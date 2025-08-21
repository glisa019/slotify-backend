package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateAdminRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.AdminRole;
import com.myslotify.slotify.exception.BadRequestException;
import com.myslotify.slotify.exception.NotFoundException;
import com.myslotify.slotify.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.myslotify.slotify.util.TenantContext;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminServiceImpl(AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse createTenantAdmin(CreateUserRequest request) {
        logger.info("Creating tenant admin with email {}", request.getEmail());
        TenantContext.clear();
        Optional<Admin> existing = adminRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new BadRequestException("Admin already exists");
        }

        Admin admin = new Admin();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPhone(request.getPhone());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(AdminRole.TENANT_ADMIN);

        adminRepository.save(admin);

        String token = jwtService.generateToken(admin);

        return new AuthResponse("Admin registered successfully", token, admin, false);
    }

    @Override
    public Admin getAdmin(UUID id) {
        logger.info("Fetching admin {}", id);
        TenantContext.clear();
        return adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin not found"));
    }

    @Override
    public Admin updateAdmin(UUID id, UpdateAdminRequest request) {
        logger.info("Updating admin {}", id);
        TenantContext.clear();
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin not found"));
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
        adminRepository.save(admin);
        return admin;
    }

}
