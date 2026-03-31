package com.example.Dating.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * JWT Token Provider - Production Grade
 * Đã tối ưu từ phiên bản bạn đưa + kinh nghiệm thực tế
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Value("${jwt.issuer:dating-app}")
    private String issuer;

    private SecretKey signingKey;

    // ================= INIT KEY =================
    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtSecret.trim());
            signingKey = Keys.hmacShaKeyFor(keyBytes);
            log.info("JWT signing key initialized successfully using Base64");
        } catch (IllegalArgumentException e) {
            signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            log.warn("JWT secret is not Base64 encoded. Using raw bytes (less secure). " +
                    "Please use Base64 encoded secret in production.");
        }
    }

    // ================= ACCESS TOKEN =================
    public String generateAccessToken(UUID userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .id(jti)
                .subject(userId.toString())
                .issuer(issuer)
                .claim("username", username)
                .claim("type", "ACCESS")
                .claim("roles", List.of("ROLE_USER"))   // hiện tại chỉ có 1 role
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();

        log.debug("Generated ACCESS token | userId={} | jti={} | expires in {}ms",
                userId, jti, accessTokenExpirationMs);
        return token;
    }

    // ================= REFRESH TOKEN =================
    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .id(jti)
                .subject(userId.toString())
                .issuer(issuer)
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();

        log.debug("Generated REFRESH token | userId={} | jti={}", userId, jti);
        return token;
    }

    // ================= VALIDATION =================
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired");
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.error("Invalid token format");
        } catch (SecurityException e) {
            log.error("Invalid JWT signature");
        } catch (Exception e) {
            log.error("Token validation failed", e);
        }
        return false;
    }

    // ================= CLAIMS EXTRACTION =================
    public Claims getAllClaims(String token) {
        return parseClaims(token);
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public List<String> getRolesFromToken(String token) {
        return parseClaims(token).get("roles", List.class);
    }

    public boolean isAccessToken(String token) {
        return "ACCESS".equals(parseClaims(token).get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(parseClaims(token).get("type", String.class));
    }

    public boolean isExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // ================= INTERNAL =================
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}