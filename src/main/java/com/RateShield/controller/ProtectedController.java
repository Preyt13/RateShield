package com.RateShield.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    @GetMapping("/api/protected")
    public String protectedEndpoint() {
        return "You have accessed a protected resource! FBI will be on your doorstep soon";
    }

    @GetMapping("/api/light")
    public String lightEndpoint() {
        return "Lightweight endpoint response.";
    }

    @GetMapping("/api/heavy")
    public String heavyEndpoint() {
        return "Heavy computation simulated. (not really)";
    }
}
