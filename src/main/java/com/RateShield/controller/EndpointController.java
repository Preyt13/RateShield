package com.RateShield.controller;

import com.RateShield.dto.RegisterEndpointRequest;
import com.RateShield.dto.EndpointResponse;
import com.RateShield.model.User;
import com.RateShield.service.EndpointService;
import com.RateShield.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/core/orgs")
public class EndpointController {

    private final EndpointService endpointService;
    private final JwtUtil jwtUtil;

    public EndpointController(EndpointService endpointService, JwtUtil jwtUtil) {
        this.endpointService = endpointService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new endpoint under a specific service.
     * Requires admin privileges and valid JWT with matching org.
     */
    @PostMapping("/{orgId}/env/{envId}/services/{serviceId}/endpoints")
    public ResponseEntity<?> registerEndpoint(@PathVariable UUID orgId,
                                              @PathVariable UUID envId,
                                              @PathVariable UUID serviceId,
                                              @RequestBody RegisterEndpointRequest request,
                                              @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractAllClaims(jwt);

        if (!"ADMIN".equals(claims.get("role", String.class))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can register endpoints");
        }

        UUID userOrgId = UUID.fromString(claims.get("orgId", String.class));
        if (!userOrgId.equals(orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cross-org registration is not allowed");
        }

        EndpointResponse endpoint = endpointService.registerEndpoint(serviceId, request);
        return ResponseEntity.ok(endpoint);
    }

    /**
     * Fetch all endpoints for a given service, must belong to caller's org.
     */
    @GetMapping("/{orgId}/env/{envId}/services/{serviceId}/endpoints")
    public ResponseEntity<?> getEndpointsForService(@PathVariable UUID orgId,
                                                    @PathVariable UUID envId,
                                                    @PathVariable UUID serviceId,
                                                    @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractAllClaims(jwt);

        UUID userOrgId = UUID.fromString(claims.get("orgId", String.class));
        if (!userOrgId.equals(orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access to other orgs is not permitted");
        }

        List<EndpointResponse> endpoints = endpointService.getEndpointsForService(serviceId);
        return ResponseEntity.ok(endpoints);
    }
}
