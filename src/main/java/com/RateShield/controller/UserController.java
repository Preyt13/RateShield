package com.RateShield.controller;

import com.RateShield.dto.LoginRequest;
import com.RateShield.dto.LoginResponse;
import com.RateShield.model.User;
import com.RateShield.service.UserService;
import com.RateShield.util.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.username, request.password);
        if (user == null || user.isAdmin()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        String token = jwtUtil.generateToken(
            user.getUsername(),
            user.getTier(),
            user.getOrgId(),
            "BASE_USER"
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }
}

