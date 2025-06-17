package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    AuthResponse createTenantAdmin(CreateUserRequest request);
}
