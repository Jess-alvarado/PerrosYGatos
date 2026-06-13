package com.auth.pyg_auth.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AccessTokenBlacklistService accessTokenBlacklistService;

    private static final String MOCKED_JTI = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final long TTL_MILLIS = 3600000L;

    @Test
    @DisplayName("Should save JTI successfully to Redis blacklist with correct TTL")
    void blacklist_shouldSaveJtiInRedisWithTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        accessTokenBlacklistService.blacklist(MOCKED_JTI, TTL_MILLIS);

        verify(valueOperations, times(1)).set(
                BLACKLIST_PREFIX + MOCKED_JTI,
                "revoked",
                TTL_MILLIS,
                TimeUnit.MILLISECONDS
        );
    }

    @Test
    @DisplayName("Should return true when JTI key exists in Redis")
    void isBlacklisted_whenKeyExists_shouldReturnTrue() {
        String redisKey = BLACKLIST_PREFIX + MOCKED_JTI;
        when(redisTemplate.hasKey(redisKey)).thenReturn(true);

        boolean result = accessTokenBlacklistService.isBlacklisted(MOCKED_JTI);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when JTI key does not exist in Redis")
    void isBlacklisted_whenKeyDoesNotExist_shouldReturnFalse() {
        String redisKey = BLACKLIST_PREFIX + MOCKED_JTI;
        when(redisTemplate.hasKey(redisKey)).thenReturn(false);

        boolean result = accessTokenBlacklistService.isBlacklisted(MOCKED_JTI);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when Redis hasKey returns null")
    void isBlacklisted_whenRedisReturnsNull_shouldReturnFalse() {
        String redisKey = BLACKLIST_PREFIX + MOCKED_JTI;
        when(redisTemplate.hasKey(redisKey)).thenReturn(null);

        boolean result = accessTokenBlacklistService.isBlacklisted(MOCKED_JTI);

        assertThat(result).isFalse();
    }
}