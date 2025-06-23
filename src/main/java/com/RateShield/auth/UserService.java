package com.RateShield.auth;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService() {
        // Plug in-memory repo by default; easily swappable later
        this.userRepo = new InMemoryUserRepo();
    }

    public boolean register(String username, String password, String tier) {
        return userRepo.registerUser(new User(username, password, tier));
    }

    public User login(String username, String password) {
        return userRepo.validateUser(username, password);
    }

    public User getUser(String username) {
        return userRepo.getUser(username);
    }
}
