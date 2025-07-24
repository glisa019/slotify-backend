package com.myslotify.slotify.service;


import com.myslotify.slotify.entity.BaseAccount;
import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.User;
import com.myslotify.slotify.entity.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.myslotify.slotify.util.TenantContext;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    private final String secretKey;

    public JwtServiceImpl(@Value("${app.jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(BaseAccount account) {
        logger.debug("Generating token for {}", account.getEmail());
        String role;
        if (account instanceof Admin admin) {
            role = admin.getRole().name();
        } else {
            role = ((User) account).getRole().name();
        }
        var builder = Jwts.builder()
                .setSubject(account.getEmail())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofHours(12))))
                .signWith(getKey());

        String tenant = TenantContext.getCurrentTenant();
        if (tenant != null) {
            builder.claim("tenant", tenant);
        }

        return builder.compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String extractEmail(String token) {
        return parseAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseAllClaims(token).get("role", String.class);
    }

    public String extractTenant(String token) {
        return parseAllClaims(token).get("tenant", String.class);
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, BaseAccount account) {
        return extractEmail(token).equals(account.getEmail());
    }
}
