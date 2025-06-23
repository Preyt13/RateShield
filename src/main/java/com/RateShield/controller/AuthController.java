package com.RateShield.controller;

import com.RateShield.auth.JwtUtil;
import com.RateShield.auth.User;
import com.RateShield.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // DTOs
    public static class RegisterRequest {
        public String username;
        public String password;
        public String tier;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (request.username == null || request.username.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Username is required for registration");
        }

        if (request.tier == null || request.tier.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Tier is required for registration");
        }
        
        boolean success = userService.register(request.username, request.password, request.tier);
        if (success) {
            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.badRequest().body("Username already exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.username, request.password);
        if (user != null) {
            String jwt = jwtUtil.generateToken(user.getUsername(), user.getTier());
            return ResponseEntity.ok(jwt);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
