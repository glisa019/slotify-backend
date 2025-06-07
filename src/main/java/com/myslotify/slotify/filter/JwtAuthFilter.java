package com.myslotify.slotify.filter;

import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.entity.Employee;
import com.myslotify.slotify.repository.AdminRepository;
import com.myslotify.slotify.repository.UserRepository;
import com.myslotify.slotify.repository.EmployeeRepository;
import com.myslotify.slotify.util.TenantContext;
import com.myslotify.slotify.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository, AdminRepository adminRepository, EmployeeRepository employeeRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (TenantContext.getCurrentTenant() == null) {
                Admin admin = adminRepository.findByEmail(email).orElse(null);
                if (admin != null && jwtService.isTokenValid(token, admin)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            admin, null, List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } else {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user == null) {
                    Employee employee = employeeRepository.findByEmail(email).orElse(null);
                    if (employee != null && jwtService.isTokenValid(token, employee)) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                employee, null, List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name())));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } else if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
