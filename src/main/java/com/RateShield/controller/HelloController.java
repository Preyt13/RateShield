package com.RateShield.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class HelloController {
    
    @GetMapping("/")
    public String home() {
        return "🔥 Welcome to RateShield!";
    }
    
}
