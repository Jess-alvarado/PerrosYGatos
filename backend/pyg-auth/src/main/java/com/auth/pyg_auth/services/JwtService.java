package com.auth.pyg_auth.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import io.jsonwebtoken.Claims;

@Service
public class JwtService {

    @Value("${secret.key}")
    private String secretKey;

    // User Token Generation
    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    /*
    public String generateRefreshToken(UserDetails user) {
        return buildToken(new HashMap<>(), user, 1000 * 60 * 60 * 24 * 7); // 7 days
    }
    */

    // Overloaded method to include extra claims
    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Helper method to get signing key
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
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
    private Claims getAllClaims(String token) {
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

    public Date getExpirationDate(String token) {
        return getExpiration(token);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    public String extractUsername(String refreshToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractUsername'");
    }
}
