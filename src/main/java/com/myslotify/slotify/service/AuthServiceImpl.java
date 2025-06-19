package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.dto.ResetPasswordRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.AdminRole;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.SubscriptionStatus;
import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.exception.NotFoundException;
import com.myslotify.slotify.exception.UnauthorizedException;
import com.myslotify.slotify.repository.AdminRepository;
import com.myslotify.slotify.repository.UserRepository;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.repository.TenantRepository;
import com.myslotify.slotify.util.TenantContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
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
        logger.info("Login attempt for {}", request.getEmail());
        if (TenantContext.getCurrentTenant() == null) {
            Admin admin = adminRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                throw new UnauthorizedException("Invalid credentials");
            }

            Boolean tenantExists = null;
            if (admin.getRole() == AdminRole.TENANT_ADMIN) {
                tenantExists = tenantRepository.findByTenantAdminEmail(admin.getEmail())
                        .map(tenant -> {
                            if (tenant.getSubscriptionStatus() == SubscriptionStatus.INACTIVE) {
                                throw new UnauthorizedException("Tenant subscription inactive");
                            }
                            return true;
                        })
                        .orElse(false);
            }

            String token = jwtService.generateToken(admin);
            return new AuthResponse("Login successful", token, admin, false, tenantExists);
        }

        Tenant tenant = tenantRepository.findBySchemaName(TenantContext.getCurrentTenant())
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        if (tenant.getSubscriptionStatus() == SubscriptionStatus.INACTIVE) {
            throw new UnauthorizedException("Tenant subscription inactive");
        }

        User account = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> employeeRepository.findByEmail(request.getEmail()).orElse(null));

        if (account == null || !passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        boolean resetRequired = account.isPasswordResetRequired();

        String token = jwtService.generateToken(account);

        String message = resetRequired ? "Password reset required" : "Login successful";

        return new AuthResponse(message, token, account, resetRequired);
    }

    @Override
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        logger.info("Resetting password for {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getTemporaryPassword(), user.getPassword())) {
            throw new UnauthorizedException("Temporary password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetRequired(false);
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse("Password reset successful", token, user, false);
    }
}
