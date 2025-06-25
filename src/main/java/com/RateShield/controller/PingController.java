package com.RateShield.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/api/ping")
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("version", "v1.0");
        response.put("status", "OK");
        return response;
    }
}
