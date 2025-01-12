package com.securefileshare.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    @GetMapping("/health/service")
    public String healthCheck() {
        return "OK";
    }
} 