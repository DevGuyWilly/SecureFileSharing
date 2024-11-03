package com.securefileshare.auth_service.service;

import com.securefileshare.auth_service.DTO.LoginRequest;
import com.securefileshare.auth_service.DTO.RegisterRequest;
import com.securefileshare.auth_service.model.User;
import com.securefileshare.auth_service.repository.UserRepository;
import com.securefileshare.auth_service.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Username is already taken!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            String jwt = jwtTokenProvider.generateToken(authentication);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    public ResponseEntity<?> logoutUser(String token) {
        // Remove "Bearer " prefix if present
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        jwtTokenProvider.invalidateToken(actualToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    public ResponseEntity<?> validateToken(String token) {
        // Remove "Bearer " prefix if present
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        if (jwtTokenProvider.validateToken(actualToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(actualToken);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username
            ));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false));
    }
}
