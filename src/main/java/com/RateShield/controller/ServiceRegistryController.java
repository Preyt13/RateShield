package com.RateShield.controller;

import com.RateShield.dto.CreateServiceRequest;
import com.RateShield.dto.ServiceInfoResponse;
import com.RateShield.model.User;
import com.RateShield.service.OrganizationService;
import com.RateShield.service.ServiceRegistryService;
import com.RateShield.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/core/orgs")
public class ServiceRegistryController {

    private final ServiceRegistryService serviceRegistryService;
    private final UserService userService;
    private final OrganizationService organizationService;

    public ServiceRegistryController(ServiceRegistryService serviceRegistryService,
                                     UserService userService,
                                     OrganizationService organizationService) {
        this.serviceRegistryService = serviceRegistryService;
        this.userService = userService;
        this.organizationService = organizationService;
    }

    @PostMapping("/{orgId}/env/{envId}/services")
    public ResponseEntity<?> registerService(@PathVariable UUID orgId,
                                             @PathVariable UUID envId,
                                             @RequestHeader("X-Username") String username,
                                             @RequestBody CreateServiceRequest request) {

        User user = userService.findByUsername(username);
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admins can register services");
        }

        if (!organizationService.doesEnvBelongToOrg(orgId, envId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid environment for your org");
        }

        request.setOrgId(orgId);
        request.setEnvId(envId);

        ServiceInfoResponse response = serviceRegistryService.createService(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orgId}/env/{envId}/services")
    public ResponseEntity<?> getServicesForEnv(@PathVariable UUID orgId,
                                               @PathVariable UUID envId,
                                               @RequestHeader("X-Username") String username) {

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (!organizationService.doesEnvBelongToOrg(orgId, envId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid env for this org");
        }

        List<ServiceInfoResponse> services = serviceRegistryService.getServicesByEnv(envId);
        return ResponseEntity.ok(services);
    }
}
