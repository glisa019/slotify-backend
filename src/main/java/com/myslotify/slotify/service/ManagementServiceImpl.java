package com.myslotify.slotify.service;

import com.myslotify.slotify.dto.CreateServiceRequest;
import com.myslotify.slotify.dto.UpdateServiceRequest;
import com.myslotify.slotify.entity.Service;
import com.myslotify.slotify.exception.NotFoundException;
import com.myslotify.slotify.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ManagementServiceImpl implements ManagementService {

    private static final Logger logger = LoggerFactory.getLogger(ManagementServiceImpl.class);

    private final ServiceRepository serviceRepository;

    public ManagementServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }


    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service getService(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
    }

    public Service createService(CreateServiceRequest request) {
        logger.info("Creating service {}", request.getName());
        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDuration(request.getDuration());
        service.setPrice(request.getPrice());

        return serviceRepository.save(service);
    }

    public Service updateService(UUID id, UpdateServiceRequest request) {
        logger.info("Updating service {}", id);
        Service service = getService(id);
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDuration(request.getDuration());
        service.setPrice(request.getPrice());
        return serviceRepository.save(service);
    }

    public void deleteService(UUID id) {
        logger.info("Deleting service {}", id);
        serviceRepository.deleteById(id);
    }
}
