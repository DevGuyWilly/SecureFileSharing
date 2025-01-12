package com.securefileshare.auth_service.service;

import com.securefileshare.auth_service.DTO.LoginRequest;
import com.securefileshare.auth_service.DTO.RegisterRequest;
import com.securefileshare.auth_service.model.User;
import com.securefileshare.auth_service.repository.UserRepository;
import com.securefileshare.auth_service.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

@Service
@Slf4j
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        try {
            log.info("Attempting to register user: {}", registerRequest.getUsername());
            
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username is already taken!"));
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(registerRequest.getRole());
            user.setCreatedAt(Instant.now());
            user.setActive(true);
            
            User savedUser = userRepository.save(user);
            log.info("User registered successfully: {}", savedUser.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "User registered successfully!",
                        "userId", savedUser.getId()
                    ));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed"));
        }
    }

    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        try {
            log.info("Attempting to login user: {}", loginRequest.getUsername());
            
            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                );

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // Generate JWT if authentication successful
            String jwt = jwtTokenProvider.generateToken(authentication);
            
            // Get user details
            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            log.info("User logged in successfully: {}", user.getUsername());
            
            return ResponseEntity.ok(Map.of(
                "token", jwt,
                "userId", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
            ));
            
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed"));
        }
    }

    public ResponseEntity<?> logoutUser(String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                jwtTokenProvider.invalidateToken(token);
                return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Logout failed"));
        }
    }

    public ResponseEntity<?> validateToken(String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            
            if (jwtTokenProvider.validateToken(actualToken)) {
                String username = jwtTokenProvider.getUsernameFromToken(actualToken);
                User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", username,
                    "userId", user.getId(),
                    "role", user.getRole()
                ));
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "valid", false,
                        "error", e.getMessage()
                    ));
        }
    }
}
