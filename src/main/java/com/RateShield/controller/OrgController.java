package com.RateShield.controller;

import com.RateShield.dto.RegisterRequest;
import com.RateShield.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/org")
public class OrgController {

    private final OrganizationService orgService;

    public OrgController(OrganizationService orgService) {
        this.orgService = orgService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.orgName == null || request.username == null || request.password == null) {
            return ResponseEntity.badRequest().body("orgName, username, and password required");
        }

        boolean created = orgService.registerOrgWithAdmin(
            request.orgName,
            request.username,
            request.password
        );

        if (created) {
            return ResponseEntity.ok("Organization and admin created");
        } else {
            return ResponseEntity.badRequest().body("Org already exists or user taken");
        }
    }
}

