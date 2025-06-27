package com.RateShield.service;

import com.RateShield.model.Organization;
import com.RateShield.model.User;
import com.RateShield.repository.OrganizationRepository;
import com.RateShield.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final OrganizationRepository orgRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepo, OrganizationRepository orgRepo) {
        this.userRepo = userRepo;
        this.orgRepo = orgRepo;
    }

    /**
     * Authenticates a user by username and password.
     * Returns null if credentials are invalid.
     */
    public User login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(u -> encoder.matches(password, u.getPassword()))
                .orElse(null);
    }

    /**
     * Checks if a user already exists with the given username.
     */
    public boolean userExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    /**
     * Registers a new user under the specified organization.
     * Returns the created User or null if org not found or user exists.
     */
    public User register(String username, String password, String tier, boolean isAdmin, UUID orgId) {
        if (userExists(username)) return null;

        Optional<Organization> orgOpt = orgRepo.findById(orgId);
        if (orgOpt.isEmpty()) return null;

        Organization org = orgOpt.get();
        User user = new User(username, encoder.encode(password), tier, isAdmin, org);
        return userRepo.save(user);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                   .orElseThrow(() -> new RuntimeException("User not found"));
    }


}
