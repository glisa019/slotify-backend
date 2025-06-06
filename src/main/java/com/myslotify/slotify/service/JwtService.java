package com.myslotify.slotify.service;

import com.myslotify.slotify.entity.User;

public interface JwtService {

    String generateToken(User user);
    String extractEmail(String token);
    String extractRole(String token);
    boolean isTokenValid(String token, User user);
}
