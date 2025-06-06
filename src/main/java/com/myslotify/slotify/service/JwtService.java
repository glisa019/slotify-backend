package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.BaseAccount;
import org.springframework.stereotype.Service;

public interface JwtService {

    String generateToken(BaseAccount account);
    String extractEmail(String token);
    String extractRole(String token);
    boolean isTokenValid(String token, BaseAccount account);
}
