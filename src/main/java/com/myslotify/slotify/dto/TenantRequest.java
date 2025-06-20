package com.myslotify.slotify.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class TenantRequest {
    private String name;
    private String schemaName;
    private String description;
    private LocalDateTime createdAt;

    private String motto;
    private String address;
    private String phone;
    private String tiktok;
    private String instagram;
    private String textColour;
    private String backgroundColour;
    private String borderColour;
    private String font;

    /**
     * Existing logo URL. Used when updating a tenant without uploading a
     * new file.
     */
    private String logoUrl;

    /**
     * Existing cover picture URL. Used when updating a tenant without a
     * new file upload.
     */
    private String coverPictureUrl;

    private MultipartFile logo;
    private MultipartFile coverPicture;
}
