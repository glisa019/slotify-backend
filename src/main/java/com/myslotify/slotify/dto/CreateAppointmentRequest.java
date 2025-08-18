package com.myslotify.slotify.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateAppointmentRequest {
    private UUID slotId;
    private UUID serviceId;
}

