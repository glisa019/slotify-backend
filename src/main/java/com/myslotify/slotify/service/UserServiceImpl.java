package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.CreateUserRequest;
import com.myslotify.slotify.dto.UpdateUserRequest;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.entity.Role;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.exception.BadRequestException;
import com.myslotify.slotify.exception.NotFoundException;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import com.myslotify.slotify.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository userRepository,
                           EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
    }

    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        return employeeRepository.findAll();
    }

    public Employee getEmployee(UUID id) {
        logger.info("Fetching employee {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    public Employee getCurrentEmployee(Authentication auth) {
        logger.info("Fetching current employee");
        String email = SecurityUtil.extractEmail(auth);
        return employeeRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    public User getUser(UUID id) {
        logger.info("Fetching user {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User getCurrentUser(Authentication auth) {
        logger.info("Fetching current user");
        String email = SecurityUtil.extractEmail(auth);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public AuthResponse createUser(CreateUserRequest request) {
        logger.info("Creating user {}", request.getEmail());
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new BadRequestException("User already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setPasswordResetRequired(false);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse("User registered successfully", token, user, user.isPasswordResetRequired());
    }

    public Employee createEmployee(CreateUserRequest request) {
        logger.info("Creating employee {}", request.getEmail());
        Optional<Employee> existing = employeeRepository.findByUserEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new BadRequestException("Employee already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.EMPLOYEE);
        user.setPasswordResetRequired(true);

        userRepository.save(user);

        Employee employee = new Employee();
        employee.setUser(user);

        Employee saved = employeeRepository.save(employee);

        notificationService.sendEmail(
                user.getEmail(),
                "Employee Account Created",
                "Your account has been created. Your temporary password is: " + request.getPassword() +
                        ". Please change it on first login.");

        return saved;
    }

    public User updateUser(UUID id, UpdateUserRequest request) {
        logger.info("Updating user {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        return user;
    }

    public void deleteEmployee(UUID id) {
        logger.info("Deleting employee {}", id);
        employeeRepository.deleteById(id);
    }

    public void deleteUser(UUID id) {
        logger.info("Deleting user {}", id);
        userRepository.deleteById(id);
    }
}
