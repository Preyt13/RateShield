package com.RateShield.service;

import com.RateShield.model.Environment;
import com.RateShield.model.Organization;
import com.RateShield.model.User;
import com.RateShield.repository.EnvironmentRepository;
import com.RateShield.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository orgRepo;
    private final UserService userService;
    private final EnvironmentRepository envRepo;

    private static final String DEFAULT_TIER = "FREE"; // TODO: Replace with enum or config later

    public OrganizationService(OrganizationRepository orgRepo,
                               UserService userService,
                               EnvironmentRepository envRepo) {
        this.orgRepo = orgRepo;
        this.userService = userService;
        this.envRepo = envRepo;
    }

    public boolean registerOrgWithAdmin(String orgName, String adminUsername, String password) {
        if (orgRepo.findByName(orgName).isPresent()) return false;

        Organization org = new Organization(orgName, DEFAULT_TIER);
        Organization savedOrg = orgRepo.save(org);

        User admin = userService.register(adminUsername, password, DEFAULT_TIER, true, savedOrg.getId());
        return admin != null;
    }

    public Organization findById(UUID id) {
        return orgRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found")); // TODO: replace with proper exception
    }

    public User getUserByUsername(String username) {
        return userService.findByUsername(username);
    }

    public Organization getOrgForUser(String username) {
        return getUserByUsername(username).getOrganization();
    }

    public boolean isUserInOrg(String username, UUID orgId) {
        return getOrgForUser(username).getId().equals(orgId);
    }

    public UUID resolveActualOrgId(String username, UUID requestedOrgId) {
        return getOrgForUser(username).getId();
    }

    public List<Environment> getEnvironmentsForUser(String username, UUID requestedOrgId) {
        UUID actualOrgId = resolveActualOrgId(username, requestedOrgId);
        return envRepo.findByOrgId(actualOrgId);
    }

    public boolean doesEnvBelongToOrg(UUID orgId, UUID envId) {
        return envRepo.findByOrgId(orgId).stream().anyMatch(env -> env.getId().equals(envId));
    }

}
