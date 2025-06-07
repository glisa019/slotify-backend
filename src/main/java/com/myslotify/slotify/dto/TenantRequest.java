package com.myslotify.slotify.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantRequest {
    private String name;
    private String schemaName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String motto;
    private String address;
    private String phone;
    private String tiktok;
    private String instagram;
    private String textColour;
    private String backgroundColour;
    private String borderColour;
    private String font;

    private byte[] logo;
    private byte[] coverPicture;
}
