package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.dto.ResetPasswordRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.SubscriptionStatus;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.repository.AdminRepository;
import com.myslotify.slotify.repository.UserRepository;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.repository.TenantRepository;
import com.myslotify.slotify.util.TenantContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           AdminRepository adminRepository,
                           EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           TenantRepository tenantRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        if (TenantContext.getCurrentTenant() == null) {
            Admin admin = adminRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            String token = jwtService.generateToken(admin);
            return new AuthResponse("Login successful", token, admin);
        }

        Tenant tenant = tenantRepository.findBySchemaName(TenantContext.getCurrentTenant())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        if (tenant.getSubscriptionStatus() != SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Tenant subscription inactive");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            Employee employee = employeeRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            if (employee.isPasswordResetRequired()) {
                throw new RuntimeException("You must reset your password before logging in.");
            }

            String token = jwtService.generateToken(employee);
            return new AuthResponse("Login successful", token, employee);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.isPasswordResetRequired()) {
            throw new RuntimeException("You must reset your password before logging in.");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse("Login successful", token, user);
    }

    @Override
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getTemporaryPassword(), user.getPassword())) {
            throw new RuntimeException("Temporary password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetRequired(false);
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse("Password reset successful", token, user);
    }
}
