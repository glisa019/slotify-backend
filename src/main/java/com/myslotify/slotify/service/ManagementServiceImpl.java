package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateServiceRequest;
import com.myslotify.slotify.dto.UpdateServiceRequest;
import com.myslotify.slotify.entity.Service;
import com.myslotify.slotify.repository.ServiceRepository;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ManagementServiceImpl implements ManagementService {

    private final ServiceRepository serviceRepository;

    public ManagementServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }


    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service getService(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public Service createService(CreateServiceRequest request) {
        Service service = new Service();
        service.setServiceId(UUID.randomUUID());
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDuration(request.getDuration());
        service.setPrice(request.getPrice());

        return serviceRepository.save(service);
    }

    public Service updateService(UUID id, UpdateServiceRequest request) {
        Service service = getService(id);
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDuration(request.getDuration());
        service.setPrice(request.getPrice());
        return serviceRepository.save(service);
    }

    public void deleteService(UUID id) {
        serviceRepository.deleteById(id);
    }
}
