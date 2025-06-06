package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.dto.ResetPasswordRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse resetPassword(ResetPasswordRequest request);
}
