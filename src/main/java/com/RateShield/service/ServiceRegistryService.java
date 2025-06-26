package com.RateShield.service;

import com.RateShield.dto.CreateServiceRequest;
import com.RateShield.dto.ServiceInfoResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing API services (base URLs) under environments and organizations.
 * Acts as the business logic layer between controller and repository.
 */
public interface ServiceRegistryService {

    /**
     * Registers a new service under a given environment and organization.
     *
     * @param request the service creation request payload
     * @return the saved service data
     */
    ServiceInfoResponse createService(CreateServiceRequest request);

    /**
     * Retrieves all services tied to a given organization.
     *
     * @param orgId the ID of the organization
     * @return list of services registered under the org
     */
    List<ServiceInfoResponse> getServicesByOrg(UUID orgId);

    /**
     * Retrieves all services registered under a specific environment.
     *
     * @param envId the ID of the environment
     * @return list of services within the environment
     */
    List<ServiceInfoResponse> getServicesByEnv(UUID envId);

    /**
     * Checks if a service with the given name already exists in the specified environment.
     *
     * @param envId the environment ID
     * @param serviceName the name of the service
     * @return true if service exists, false otherwise
     */
    boolean serviceExistsInEnv(UUID envId, String serviceName);
}

