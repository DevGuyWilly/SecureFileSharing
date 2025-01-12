package com.securefileshare.auth_service.security;

import com.securefileshare.auth_service.model.User;
import com.securefileshare.auth_service.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenProvider {

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private Key key;
    
    // Thread-safe set to store invalidated tokens
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private UserRepository userRepository;

    @PostConstruct
    protected void init() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            // First check if token is blacklisted
            if (blacklistedTokens.contains(authToken)) {
                return false;
            }

            // Then verify the token's signature and expiration
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void invalidateToken(String token) {
        if (token != null && validateToken(token)) {
            blacklistedTokens.add(token);
        }
    }

    public String getUserIdFromToken(String token) {
        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    // Optional: Method to clean up expired tokens from blacklist
    public void cleanupBlacklist() {
        Date now = new Date();
        blacklistedTokens.removeIf(token -> {
            try {
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                return claims.getExpiration().before(now);
            } catch (Exception e) {
                return true; // Remove invalid tokens
            }
        });
    }
}
