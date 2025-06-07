package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.dto.ResetPasswordRequest;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.repository.AdminRepository;
import com.myslotify.slotify.repository.UserRepository;
import com.myslotify.slotify.util.TenantContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

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
