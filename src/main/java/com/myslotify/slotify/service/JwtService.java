package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.BaseAccount;
import org.springframework.stereotype.Service;

public interface JwtService {

    String generateToken(BaseAccount account);
    String extractEmail(String token);
    String extractRole(String token);
    /**
     * Extract the tenant identifier stored in the token, if present.
     *
     * @param token JWT string
     * @return tenant schema name or {@code null} when not available
     */
    String extractTenant(String token);
    boolean isTokenValid(String token, BaseAccount account);
}
