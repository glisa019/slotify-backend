package com.myslotify.slotify.dto;

import com.myslotify.slotify.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    public String message;
    public String token;
    public User user;
}
