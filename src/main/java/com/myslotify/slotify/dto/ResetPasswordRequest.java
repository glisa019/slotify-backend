package com.myslotify.slotify.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String temporaryPassword;
    private String newPassword;
}
