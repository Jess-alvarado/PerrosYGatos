package com.auth.pyg_auth.services;

import com.auth.pyg_auth.models.BlacklistedAccessToken;
import com.auth.pyg_auth.repositories.BlacklistedAccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistService {

    private final BlacklistedAccessTokenRepository blacklistedAccessTokenRepository;

    @Transactional
    public void blacklistToken(String token, Date expiresAt) {
        if (blacklistedAccessTokenRepository.existsByToken(token)) {
            return;
        }

        BlacklistedAccessToken blacklistedToken = BlacklistedAccessToken.builder()
                .token(token)
                .expiresAt(LocalDateTime.ofInstant(expiresAt.toInstant(), ZoneId.systemDefault()))
                .build();

        blacklistedAccessTokenRepository.save(blacklistedToken);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedAccessTokenRepository.existsByToken(token);
    }
}