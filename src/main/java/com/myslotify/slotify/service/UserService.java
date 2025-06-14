package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateUserRequest;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    List<Employee> getAllEmployees();
    Employee getEmployee(UUID id);
    Employee getCurrentEmployee(Authentication auth);
    List<User> getAllUsers();
    User getUser(UUID id);
    User getCurrentUser(Authentication auth);
    AuthResponse createUser(CreateUserRequest request);
    Employee createEmployee(CreateUserRequest request);
    User updateUser(UUID id, UpdateUserRequest request);
    void deleteEmployee(UUID id);
    void deleteUser(UUID id);
}
