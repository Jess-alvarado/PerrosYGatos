package com.auth.pyg_auth.services;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, Date expiration) {
        if (token == null) {
            return;
        }
        Instant expiryInstant = expiration != null ? expiration.toInstant() : Instant.MAX;
        if (expiryInstant.isAfter(Instant.now())) {
            blacklistedTokens.put(token, expiryInstant);
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null) {
            return false;
        }
        Instant expiryInstant = blacklistedTokens.get(token);
        if (expiryInstant == null) {
            return false;
        }
        if (expiryInstant.isBefore(Instant.now())) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }
}
