package com.auth.pyg_auth.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.auth.pyg_auth.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import io.jsonwebtoken.Claims;


@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    // User token generation
    public String getToken(UserDetails userDetails) {
        // Cast to our User class which contains additional information
        User user = (User) userDetails;

    // Create map of claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());                    // User ID
        claims.put("role", user.getRole().getName());       // Role (ROLE_OWNER, ROLE_PROFESSIONAL)
        claims.put("firstname", user.getFirstname());       // First name
        claims.put("lastname", user.getLastname());         // Last name

        return getToken(claims, user);
    }

    // Overloaded method to include extra claims
    private String getToken(Map<String, Object> claims, UserDetails user) {
        return Jwts.builder()
                .setClaims(claims)                          // Custom claims (uid, role, etc)
                .setSubject(user.getUsername())             // Username as subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Helper method to obtain the signing key
    private Key getKey() {
        try {
            System.out.println("Attempting to use JWT_SECRET directly as Base64...");
            // 1. Primero intenta decodificar como Base64
            // 1. First try to decode as Base64
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        } catch (Exception e) {
            System.out.println("Secret not Base64, attempting to convert from UTF-8...");
            // 2. Si falla, usa la clave como está en UTF-8 y la convierte a Base64
            try {
                byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
                String base64Key = Base64.getEncoder().encodeToString(keyBytes);
                System.out.println("Key successfully converted to Base64");
                return Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
            } catch (Exception e2) {
                System.out.println("Fatal error processing JWT_SECRET");
                throw new RuntimeException("Error processing JWT_SECRET. Ensure it is a valid string.", e2);
            }
        }
    }

    // Generate test token with simple claims
    public String generateTestToken(Map<String, String> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token Validation
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Verify that the token is valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Extract all claims from the token
    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract specific claim using a resolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Check if the token has expired
    private Date getExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
