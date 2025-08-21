package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateAdminRequest;
import com.myslotify.slotify.entity.Admin;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface AdminService {
    AuthResponse createTenantAdmin(CreateUserRequest request);
    Admin getAdmin(UUID id);
    Admin updateAdmin(UUID id, UpdateAdminRequest request);
}
