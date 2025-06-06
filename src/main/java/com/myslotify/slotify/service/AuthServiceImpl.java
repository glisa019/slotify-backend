package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.dto.ResetPasswordRequest;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.isPasswordResetRequired()) {
            throw new RuntimeException("You must reset your password before logging in.");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse("Login successful", token);
    }

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
        return new AuthResponse("Password reset successful", token);
    }
}
