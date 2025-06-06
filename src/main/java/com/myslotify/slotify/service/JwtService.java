package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.entity.Admin;
import org.springframework.stereotype.Service;

public interface JwtService {

    String generateToken(User user);
    String generateToken(Admin admin);
    String extractEmail(String token);
    String extractRole(String token);
    boolean isTokenValid(String token, User user);
    boolean isTokenValid(String token, Admin admin);
}
