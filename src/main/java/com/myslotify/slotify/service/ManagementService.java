package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateServiceRequest;
import com.myslotify.slotify.dto.UpdateServiceRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ManagementService {
    List<com.myslotify.slotify.entity.Service> getAllServices();
    com.myslotify.slotify.entity.Service getService(UUID id);
    com.myslotify.slotify.entity.Service updateService(UUID id, UpdateServiceRequest request);
    com.myslotify.slotify.entity.Service createService(CreateServiceRequest request);
    void deleteService(UUID id);
}
