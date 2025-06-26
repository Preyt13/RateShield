package com.RateShield.service;

import com.RateShield.dto.RegisterEndpointRequest;
import com.RateShield.dto.EndpointResponse;

import java.util.List;
import java.util.UUID;

public interface EndpointService {

    EndpointResponse registerEndpoint(UUID serviceId, RegisterEndpointRequest request);

    List<EndpointResponse> getEndpointsForService(UUID serviceId);
}
