package com.RateShield.controller;

import com.RateShield.dto.CreateServiceRequest;
import com.RateShield.dto.ServiceInfoResponse;
import com.RateShield.service.ServiceRegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
public class ServiceRegistryController {

    private final ServiceRegistryService serviceRegistryService;

    public ServiceRegistryController(ServiceRegistryService serviceRegistryService) {
        this.serviceRegistryService = serviceRegistryService;
    }

    /**
     * Register a new service under a given org and environment.
     * The orgId should match the org associated with the authenticated user.
     * For now, orgId is passed via header for simplicity — in real systems, this is extracted from a verified JWT.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerService(
            @RequestHeader("X-Org-ID") UUID requesterOrgId,
            @RequestBody CreateServiceRequest request
    ) {
        // Enforce org ownership: prevent registering into someone else's org
        if (!requesterOrgId.equals(request.getOrgId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Unauthorized: orgId in request does not match your org.");
        }

        ServiceInfoResponse response = serviceRegistryService.createService(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetch all services for the calling user's organization.
     * Uses the X-Org-ID header to simulate token-derived org ID.
     */
    @GetMapping("/my-org")
    public ResponseEntity<List<ServiceInfoResponse>> getMyOrgServices(
            @RequestHeader("X-Org-ID") UUID orgId
    ) {
        List<ServiceInfoResponse> services = serviceRegistryService.getServicesByOrg(orgId);
        return ResponseEntity.ok(services);
    }

    /**
     * Fetch all services in a specific environment (must belong to caller's org).
     * The backend should verify env ownership; for now we assume valid access via header.
     */
    @GetMapping("/env/{envId}")
    public ResponseEntity<List<ServiceInfoResponse>> getServicesByEnv(
            @RequestHeader("X-Org-ID") UUID orgId,
            @PathVariable UUID envId
    ) {
        // TODO: Add enforcement that envId belongs to orgId
        List<ServiceInfoResponse> services = serviceRegistryService.getServicesByEnv(envId);
        return ResponseEntity.ok(services);
    }
}

