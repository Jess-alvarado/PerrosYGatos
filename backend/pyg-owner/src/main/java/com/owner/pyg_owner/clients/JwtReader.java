package com.owner.pyg_owner.clients;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtReader {

    private final String secret = System.getProperty("JWT_SECRET", "change_me");

    // Extracts the user ID ("uid") from the JWT token in the Authorization header
    public Long extractUserId(String bearer) {
        String token = bearer.replace("Bearer ", "").trim();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // "uid" is stored as a Number in the claims
        return claims.get("uid", Number.class).longValue();
    }
}