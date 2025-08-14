package com.example.demo.security;


// src/main/java/com/example/shopee/security/JwtUtils.java

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @PostConstruct
    public void logSecret() {
        logger.info(">> DEBUG jwtSecret = {}", jwtSecret);
    }

    
    // private final Key key = Keys.hmacShaKeyFor("uHUlTqpv/kRdm/yTCPsS1Xqn1JPTYSXzyShj0Zgo3knVGONTjq1+ABFadFFjvKg".getBytes());

    public String generateToken(Authentication authentication) {
        final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        String username = authentication.getName(); // Extract username from Authentication
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token);
        return true;

    }
}