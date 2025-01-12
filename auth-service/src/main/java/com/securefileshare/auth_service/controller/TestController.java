package com.securefileshare.auth_service.controller;

import com.securefileshare.auth_service.model.User;
import com.securefileshare.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TestController {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test-db")
    public Map<String, Object> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Test MongoDB connection
            String dbName = mongoTemplate.getDb().getName();
            response.put("connection", "MongoDB connection successful!");
            response.put("database", dbName);
            
            // List all users
            List<User> users = userRepository.findAll();
            response.put("userCount", users.size());
            response.put("users", users.stream()
                .map(user -> Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "active", user.isActive()
                ))
                .collect(Collectors.toList()));
            
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }
} 