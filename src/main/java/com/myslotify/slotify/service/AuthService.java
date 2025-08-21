package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.dto.ResetPasswordRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse resetPassword(ResetPasswordRequest request);
    AuthResponse resetAdminPassword(ResetPasswordRequest request);
}
