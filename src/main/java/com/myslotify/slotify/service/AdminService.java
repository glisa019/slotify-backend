package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.entity.Admin;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    Admin createTenantAdmin(CreateUserRequest request);
}
