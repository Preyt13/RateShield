package com.RateShield.service.impl;

import com.RateShield.dto.RegisterEndpointRequest;
import com.RateShield.dto.EndpointResponse;
import com.RateShield.model.Endpoint;
import com.RateShield.repository.EndpointRepository;
import com.RateShield.service.EndpointService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EndpointServiceImpl implements EndpointService {

    private final EndpointRepository endpointRepo;

    public EndpointServiceImpl(EndpointRepository endpointRepo) {
        this.endpointRepo = endpointRepo;
    }

    @Override
    public EndpointResponse registerEndpoint(UUID serviceId, RegisterEndpointRequest request) {
        // Check if this exact endpoint already exists for the service
        boolean exists = endpointRepo.findByServiceIdAndPathAndMethod(
            serviceId, request.getPath(), request.getMethod()
        ).isPresent();

        if (exists) {
            throw new RuntimeException("Endpoint already registered for this service with same path and method.");
        }

        Endpoint endpoint = Endpoint.builder()
                .path(request.getPath())
                .method(request.getMethod())
                .groupLabel(request.getGroupLabel())
                .rateLimitTier(request.getRateLimitTier())
                .scopeKey(request.getScopeKey())
                .serviceId(serviceId)
                .build();

        endpointRepo.save(endpoint);

        return mapToResponse(endpoint);
    }

    @Override
    public List<EndpointResponse> getEndpointsForService(UUID serviceId) {
        return endpointRepo.findByServiceId(serviceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EndpointResponse mapToResponse(Endpoint e) {
        return EndpointResponse.builder()
                .id(e.getId())
                .path(e.getPath())
                .method(e.getMethod())
                .groupLabel(e.getGroupLabel())
                .rateLimitTier(e.getRateLimitTier())
                .scopeKey(e.getScopeKey())
                .build();
    }

    @Override
    public List<Endpoint> getRawEndpointsForService(UUID serviceId) {
        return endpointRepo.findByServiceId(serviceId);
    }

}
