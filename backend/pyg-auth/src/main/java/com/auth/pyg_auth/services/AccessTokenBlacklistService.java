package com.auth.pyg_auth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void blacklist(String jti, long ttlMillis) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + jti,
                "revoked",
                ttlMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(BLACKLIST_PREFIX + jti)
        );
    }
}