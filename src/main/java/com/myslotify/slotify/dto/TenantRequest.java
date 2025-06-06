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
}
