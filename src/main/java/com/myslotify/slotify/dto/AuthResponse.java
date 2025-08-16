package com.myslotify.slotify.dto;

import com.myslotify.slotify.entity.BaseAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    public String message;
    public String token;
    public BaseAccount account;
    public boolean passwordResetRequired;
    public Boolean tenantExists;
    public String tenantKey;

    public AuthResponse(String message, String token, BaseAccount account, boolean passwordResetRequired) {
        this(message, token, account, passwordResetRequired, null, null);
    }

    public AuthResponse(String message, String token, BaseAccount account, boolean passwordResetRequired, Boolean tenantExists) {
        this(message, token, account, passwordResetRequired, tenantExists, null);
    }

    public AuthResponse(String message, String token, BaseAccount account, boolean passwordResetRequired, Boolean tenantExists, String tenantKey) {
        this.message = message;
        this.token = token;
        this.account = account;
        this.passwordResetRequired = passwordResetRequired;
        this.tenantExists = tenantExists;
        this.tenantKey = tenantKey;
    }
}
