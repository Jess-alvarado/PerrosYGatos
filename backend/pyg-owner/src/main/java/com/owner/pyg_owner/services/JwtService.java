package com.owner.pyg_owner.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${secret}")
    private String secretKey;

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.warn("Token con firma inválida", e);
            return false;
        } catch (Exception e) {
            log.error("Error verificando token", e);
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
            .parseClaimsJws(token)
            .getBody();
    }

    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long getUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String getRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}
