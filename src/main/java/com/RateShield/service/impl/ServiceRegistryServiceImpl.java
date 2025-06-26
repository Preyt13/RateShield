package com.RateShield.service.impl;

import com.RateShield.dto.CreateServiceRequest;
import com.RateShield.dto.ServiceInfoResponse;
import com.RateShield.model.RegisteredService;
import com.RateShield.repository.RegisteredServiceRepository;
import com.RateShield.service.ServiceRegistryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of APIService for managing service registration and retrieval.
 */
@Service
public class ServiceRegistryServiceImpl implements ServiceRegistryService {

    private final RegisteredServiceRepository serviceRepository;

    public ServiceRegistryServiceImpl(RegisteredServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceInfoResponse createService(CreateServiceRequest request) {
        RegisteredService service = RegisteredService.builder()
                .name(request.getName())
                .baseUrl(request.getBaseUrl())
                .envId(request.getEnvId())
                .orgId(request.getOrgId())
                .rateLimitTier(request.getRateLimitTier())
                .createdAt(Instant.now())
                .build();

        serviceRepository.save(service);
        return mapToResponse(service);
    }

    @Override
    public List<ServiceInfoResponse> getServicesByOrg(UUID orgId) {
        return serviceRepository.findByOrgId(orgId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceInfoResponse> getServicesByEnv(UUID envId) {
        return serviceRepository.findByEnvId(envId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean serviceExistsInEnv(UUID envId, String serviceName) {
        return serviceRepository.existsByNameAndEnvId(serviceName,envId);
    }

    /**
     * Maps a Service entity to a ServiceResponse DTO.
     */
    private ServiceInfoResponse mapToResponse(RegisteredService svc) {
        return ServiceInfoResponse.builder()
                .id(svc.getId())
                .name(svc.getName())
                .baseUrl(svc.getBaseUrl())
                .rateLimitTier(svc.getRateLimitTier())
                .build();
    }
}

