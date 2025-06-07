package com.myslotify.slotify.dto;

import com.myslotify.slotify.entity.Tenant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TenantResponse {
    private Tenant tenant;
    private String paymentUrl;
}
