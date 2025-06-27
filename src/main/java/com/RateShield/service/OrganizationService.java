package com.RateShield.service;

import com.RateShield.model.Organization;
import com.RateShield.model.User;
import com.RateShield.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Handles organization-level operations such as creation and lookup.
 */
@Service
public class OrganizationService {

    private final OrganizationRepository orgRepo;
    private final UserService userService;

    private static final String DEFAULT_TIER = "FREE"; // TODO: Replace with enum or config later

    public OrganizationService(OrganizationRepository orgRepo, UserService userService) {
        this.orgRepo = orgRepo;
        this.userService = userService;
    }

    /**
     * Registers a new organization with an associated admin user.
     * Returns false if org name is already taken or user registration fails.
     */
    public boolean registerOrgWithAdmin(String orgName, String adminUsername, String password) {
        if (orgRepo.findByName(orgName).isPresent()) return false;

        Organization org = new Organization(orgName, DEFAULT_TIER);
        Organization savedOrg = orgRepo.save(org);

        User admin = userService.register(adminUsername, password, DEFAULT_TIER, true, savedOrg.getId());
        return admin != null;
    }

    /**
     * Fetches an organization by ID or throws if not found.
     */
    public Organization findById(UUID id) {
        return orgRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found")); // TODO: replace with proper exception
    }
}
