package com.RateShield.controller;

import com.RateShield.model.Environment;
import com.RateShield.model.Organization;
import com.RateShield.model.User;
import com.RateShield.repository.EnvironmentRepository;
import com.RateShield.repository.OrganizationRepository;
import com.RateShield.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/core/orgs")
public class OrgEnvCoreController {

    private final UserRepository userRepo;
    private final OrganizationRepository orgRepo;
    private final EnvironmentRepository envRepo;

    public OrgEnvCoreController(UserRepository userRepo,
                            OrganizationRepository orgRepo,
                            EnvironmentRepository envRepo) {
        this.userRepo = userRepo;
        this.orgRepo = orgRepo;
        this.envRepo = envRepo;
    }

    /**
     * Fetch organization info for the currently authenticated user.
     * Even if orgId in path doesn't match user's actual org, the user is always rerouted to their own org.
     */
    @GetMapping("/{orgId}")
    public ResponseEntity<?> getOrgInfo(@PathVariable UUID orgId,
                                        @RequestHeader("X-Username") String username) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        Organization actualOrg = user.getOrganization();

        return ResponseEntity.ok(Map.of(
                "id", actualOrg.getId(),
                "name", actualOrg.getName(),
                "tier", actualOrg.getPlan() 
        ));
    }

    /**
     * Return all environments in the user's org.
     * The orgId in path is ignored if it doesn't match caller's org — rerouted silently.
     */
    @GetMapping("/{orgId}/env")
    public ResponseEntity<?> getEnvironments(@PathVariable Long orgId,
                                             @RequestHeader("X-Username") String username) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        Long actualOrgId = user.getOrganization().getId();
        if (!actualOrgId.equals(orgId)) {
            orgId = actualOrgId; // Reroute
        }

        List<Environment> envs = envRepo.findByOrgId(orgId);
        return ResponseEntity.ok(envs);
    }
}
