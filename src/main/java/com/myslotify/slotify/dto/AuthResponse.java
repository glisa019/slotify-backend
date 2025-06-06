package com.myslotify.slotify.dto;

import com.myslotify.slotify.entity.BaseAccount;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    public String message;
    public String token;
    public BaseAccount account;
}
