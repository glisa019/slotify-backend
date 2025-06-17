package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.AdminRole;
import com.myslotify.slotify.exception.BadRequestException;
import com.myslotify.slotify.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

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
}
