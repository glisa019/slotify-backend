package com.myslotify.slotify.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateAppointmentForCustomerRequest {
    private UUID slotId;
    private UUID serviceId;
    private UUID customerId;
}

