package com.RateShield.controller;

import com.RateShield.config.PlanRateConfig;
import com.RateShield.config.PlanRateConfig.RateLimitParams;
import com.RateShield.dto.AccessSimulationRequest;
import com.RateShield.model.*;
import com.RateShield.service.*;
import com.RateShield.util.RateLimitResolverUtil;
import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/simulate")
public class AccessSimulationController {

    private final TokenService tokenService;
    private final EndpointService endpointService;
    private final ServiceRegistryService serviceRegistryService;
    private final OrganizationService organizationService;
    private final EnvironmentService environmentService;
    @Autowired
    private final RateLimitResolverUtil rateLimitResolverUtil;

    public AccessSimulationController(
            TokenService tokenService,
            EndpointService endpointService,
            ServiceRegistryService serviceRegistryService,
            OrganizationService organizationService,
            EnvironmentService environmentService,
            PlanRateConfig planRateConfig
    ) {
        this.tokenService = tokenService;
        this.endpointService = endpointService;
        this.serviceRegistryService = serviceRegistryService;
        this.organizationService = organizationService;
        this.environmentService = environmentService;
        this.rateLimitResolverUtil = new RateLimitResolverUtil(planRateConfig);
    }

    @PostMapping
    public ResponseEntity<?> simulateAccess(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody AccessSimulationRequest request) {
        String token = authHeader.replace("Bearer ", "");

        // JWT Token case
        if (token.split("\\.").length == 3) {
            if (!tokenService.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(Map.of(
                        "valid", false,
                        "error", "JWT is invalid or expired"
                ));
            }

            Claims claims = tokenService.extractClaims(token);
            UUID tokenOrgId = UUID.fromString(claims.get("orgId", String.class));

            if (!tokenOrgId.equals(request.getOrgId())) {
                return ResponseEntity.status(403).body(Map.of(
                        "valid", false,
                        "error", "Org mismatch — JWT belongs to a different organization"
                ));
            }

            Organization org = organizationService.findById(tokenOrgId);
            Environment env = environmentService.getBaseEnvironmentForOrg(tokenOrgId);

            List<Endpoint> endpoints = endpointService.getRawEndpointsForService(request.getServiceId());
            Endpoint matchedEndpoint = endpoints.stream()
                    .filter(e -> e.getPath().equalsIgnoreCase(request.getEndpointPath()) &&
                                 e.getMethod().equalsIgnoreCase(request.getMethod()))
                    .findFirst()
                    .orElse(null);

            if (matchedEndpoint == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "valid", false,
                        "error", "Endpoint not found in service registry"
                ));
            }

            RateLimitParams limits = rateLimitResolverUtil.resolveRateLimit(
                    matchedEndpoint,
                    null,
                    env,
                    org
            );

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "access", "GRANTED (JWT)",
                    "note", "JWT tokens bypass scope/method checks but must match org",
                    "rateLimit", Map.of(
                        "capacity", limits.capacity,
                        "refillRate", limits.refillRate
                    )
            ));
        }

        // UUID Token case
        Optional<ApiToken> optToken = tokenService.getValidApiToken(token);
        if (optToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of(
                    "valid", false,
                    "error", "API token is missing, revoked, or expired"
            ));
        }

        ApiToken apiToken = optToken.get();

        if (!apiToken.getOrgId().equals(request.getOrgId())) {
            return ResponseEntity.status(403).body(Map.of(
                    "valid", false,
                    "error", "Org mismatch — token does not belong to this organization"
            ));
        }

        String scopeKey = request.getEndpointPath().replaceAll("/+$", "") + ":" + request.getMethod().toUpperCase();
        Set<String> allowedScopes = new HashSet<>(Arrays.asList(apiToken.getScopes().split(",")));

        if (!allowedScopes.contains(scopeKey)) {
            return ResponseEntity.status(403).body(Map.of(
                    "valid", false,
                    "error", "Scope denied — token does not include access to this method + path",
                    "expectedScope", scopeKey,
                    "allowedScopes", allowedScopes
            ));
        }

        List<Endpoint> endpoints = endpointService.getRawEndpointsForService(request.getServiceId());
        Endpoint matchedEndpoint = endpoints.stream()
                .filter(e -> e.getPath().equalsIgnoreCase(request.getEndpointPath()) &&
                             e.getMethod().equalsIgnoreCase(request.getMethod()))
                .findFirst()
                .orElse(null);

        if (matchedEndpoint == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "valid", false,
                    "error", "Requested endpoint does not exist in service registry"
            ));
        }

        Organization org = organizationService.findById(apiToken.getOrgId());
        Environment env = environmentService.getBaseEnvironmentForOrg(org.getId());
        RegisteredService service = serviceRegistryService.getServiceById(request.getServiceId())
            .orElseThrow(() -> new RuntimeException("Service not found"));

        RateLimitParams limits = rateLimitResolverUtil.resolveRateLimit(
                matchedEndpoint,
                service,
                env,
                org
        );

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "access", "GRANTED (UUID)",
                "tier", apiToken.getTier(),
                "rateLimit", Map.of(
                        "capacity", limits.capacity,
                        "refillRate", limits.refillRate
                ),
                "scopes", allowedScopes,
                "expiresAt", apiToken.getExpiresAt()
        ));
    }
}
