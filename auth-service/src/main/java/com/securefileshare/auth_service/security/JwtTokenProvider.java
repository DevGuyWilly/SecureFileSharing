package com.securefileshare.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Base64;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpirationMs;

    private Key key;
    private final Map<String, Date> invalidatedTokens = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Use a secure key generation method
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
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

    public boolean validateToken(String token) {
        try {
            // Check if token is invalidated
            if (invalidatedTokens.containsKey(token)) {
                Date invalidationTime = invalidatedTokens.get(token);
                if (invalidationTime.after(new Date())) {
                    return false;
                } else {
                    // Clean up expired invalidation
                    invalidatedTokens.remove(token);
                }
            }

            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void invalidateToken(String token) {
        // Store invalidation time as token expiration time
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            Date expirationDate = claims.getExpiration();
            invalidatedTokens.put(token, expirationDate);
        } catch (JwtException e) {
            // If token can't be parsed, invalidate it immediately
            invalidatedTokens.put(token, new Date(System.currentTimeMillis() + jwtExpirationMs));
        }
    }

    // Cleanup method to run periodically
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupInvalidatedTokens() {
        Date now = new Date();
        invalidatedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}
