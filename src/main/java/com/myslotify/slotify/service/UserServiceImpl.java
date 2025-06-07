package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateUserRequest;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.Role;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private NotificationService notificationService;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AuthResponse createUser(CreateUserRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setPasswordResetRequired(Role.EMPLOYEE.equals(request.getRole()));

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse("User registered successfully", token, user);
    }

    public Employee createEmployee(CreateUserRequest request) {
        User user = (User) createUser(request).getAccount();

        Employee employee = new Employee();
        employee.setUser(user);

        Employee saved = employeeRepository.save(employee);
        notificationService.sendEmail(
                user.getEmail(),
                "Employee Account Created",
                "Your account has been created. Please log in to start managing appointments.");

        return saved;
    }

    public User updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        return user;
    }

    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
