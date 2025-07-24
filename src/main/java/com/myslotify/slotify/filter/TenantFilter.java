package com.myslotify.slotify.filter;

import com.myslotify.slotify.util.TenantContext;
import com.myslotify.slotify.service.JwtService;
import org.slf4j.MDC;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public TenantFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    tenantId = jwtService.extractTenant(authHeader.substring(7));
                } catch (Exception ignored) {
                }
            }
        }

        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
            MDC.put("tenant", tenantId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            MDC.remove("tenant");
        }
    }
}
