package com.RateShield.service;

import com.RateShield.model.Organization;
import com.RateShield.model.User;
import com.RateShield.repository.OrganizationRepository;
import com.RateShield.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    private final OrganizationRepository orgRepo;
    private final UserService userService;

    public OrganizationService(OrganizationRepository orgRepo, UserService userService) {
        this.orgRepo = orgRepo;
        this.userService = userService;
    }

    public boolean registerOrgWithAdmin(String orgName, String adminUsername, String password) {
        if (orgRepo.findByName(orgName).isPresent()) return false;

        Organization org = new Organization(orgName);
        orgRepo.save(org);

        User admin = userService.register(adminUsername, password, "ADMIN", true, org.getId());
        return admin != null;
    }
}

