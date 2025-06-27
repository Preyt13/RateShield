package com.RateShield.controller;

import com.RateShield.dto.CreateServiceRequest;
import com.RateShield.dto.ServiceInfoResponse;
import com.RateShield.model.User;
import com.RateShield.repository.EnvironmentRepository;
import com.RateShield.repository.UserRepository;
import com.RateShield.service.ServiceRegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/core/orgs")
public class ServiceRegistryController {

    private final ServiceRegistryService serviceRegistryService;
    private final UserRepository userRepo;
    private final EnvironmentRepository envRepo;

    public ServiceRegistryController(ServiceRegistryService serviceRegistryService,
                                     UserRepository userRepo,
                                     EnvironmentRepository envRepo) {
        this.serviceRegistryService = serviceRegistryService;
        this.userRepo = userRepo;
        this.envRepo = envRepo;
    }

    /**
     * Register a new service under a specific org + env.
     * Authenticated user must be admin and belong to that org.
     */
    @PostMapping("/{orgId}/env/{envId}/services")
    public ResponseEntity<?> registerService(@PathVariable UUID orgId,
                                             @PathVariable UUID envId,
                                             @RequestHeader("X-Username") String username,
                                             @RequestBody CreateServiceRequest request) {

        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admins can register services");
        }

        UUID userOrgId = user.getOrganization().getId();
        if (!userOrgId.equals(orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cross-org registration not allowed");
        }

        // Validate env belongs to org
        boolean envValid = envRepo.findByOrgId(userOrgId).stream()
                .anyMatch(env -> env.getId().equals(envId));

        if (!envValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid environment for your org");
        }

        request.setOrgId(orgId);
        request.setEnvId(envId);

        ServiceInfoResponse response = serviceRegistryService.createService(request);
        return ResponseEntity.ok(response);
    }

    /**
     * View all services in an environment within your own org
     */
    @GetMapping("/{orgId}/env/{envId}/services")
    public ResponseEntity<?> getServicesForEnv(@PathVariable UUID orgId,
                                               @PathVariable UUID envId,
                                               @RequestHeader("X-Username") String username) {

        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        UUID userOrgId = user.getOrganization().getId();
        if (!userOrgId.equals(orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access to other orgs denied");
        }

        boolean envValid = envRepo.findByOrgId(userOrgId).stream()
                .anyMatch(env -> env.getId().equals(envId));

        if (!envValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid env for this org");
        }

        List<ServiceInfoResponse> services = serviceRegistryService.getServicesByEnv(envId);
        return ResponseEntity.ok(services);
    }
}
