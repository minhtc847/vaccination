package com.vaccination.BE.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenEmailUtil {

    @Value("${jwt.signerKey}")
    private String base64SignerKey;

    private Key SIGNER_KEY;

    @PostConstruct
    public void init() {
        // Generating a secure key for HS512
        byte[] keyBytes = Base64.getDecoder().decode(base64SignerKey);
        this.SIGNER_KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Long scheduleId) {
        return Jwts.builder()
                .claim("username", username)
                .claim("scheduleId", scheduleId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token valid for 1 day
                .signWith(SIGNER_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNER_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
