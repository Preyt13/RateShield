package com.RateShield.controller;

import com.RateShield.model.Environment;
import com.RateShield.model.Organization;
import com.RateShield.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/core/orgs")
public class OrgEnvCoreController {

    private final OrganizationService orgService;

    public OrgEnvCoreController(OrganizationService orgService) {
        this.orgService = orgService;
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<?> getOrgInfo(@PathVariable UUID orgId,
                                        @RequestHeader("X-Username") String username) {
        try {
            Organization actualOrg = orgService.getOrgForUser(username);
            return ResponseEntity.ok(Map.of(
                    "id", actualOrg.getId(),
                    "name", actualOrg.getName(),
                    "tier", actualOrg.getPlan()
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body("User or Organization not found");
        }
    }

    @GetMapping("/{orgId}/env")
    public ResponseEntity<?> getEnvironments(@PathVariable UUID orgId,
                                             @RequestHeader("X-Username") String username) {
        try {
            List<Environment> envs = orgService.getEnvironmentsForUser(username, orgId);
            return ResponseEntity.ok(envs);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body("User or Organization not found");
        }
    }
}
