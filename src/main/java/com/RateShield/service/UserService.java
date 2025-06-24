package com.RateShield.service;

import com.RateShield.model.User;
import com.RateShield.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(u -> encoder.matches(password, u.getPassword()))
                .orElse(null);
    }

    public boolean userExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public User register(String username, String password, String tier, boolean isAdmin, Long orgId) {
        if (userExists(username)) return null;

        User user = new User(username, encoder.encode(password), tier, isAdmin, orgId);
        return userRepo.save(user);
    }
}


