package com.gateway.pyg_gateway.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String secretKey;
    private Key signingKey;

    @BeforeEach
    void setUp() {
        signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretKey = Base64.getEncoder().encodeToString(signingKey.getEncoded());

        ReflectionTestUtils.setField(jwtAuthenticationFilter, "secretKey", secretKey);

        lenient().when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("Should allow public paths to pass through without token")
    void filter_withPublicPath_shouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/auth/login").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        verify(filterChain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when Authorization header is missing")
    void filter_withMissingHeader_shouldReturn401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/perros").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("Should mutate request and inject custom headers when token is completely valid")
    void filter_withValidToken_shouldInjectHeadersAndPass() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", 123L);
        claims.put("role", "ROLE_OWNER");
        claims.put("jti", "mock-jti-123");

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("test@perrosygatos.cl")
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(signingKey)
                .compact();

        when(redisTemplate.hasKey("blacklist:mock-jti-123")).thenReturn(false);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/perros")
                .header("Authorization", "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        verify(filterChain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when token is blacklisted in Redis")
    void filter_withBlacklistedToken_shouldReturn401() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", 123L);
        claims.put("role", "ROLE_OWNER");
        claims.put("jti", "blacklisted-jti");

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("test@perrosygatos.cl")
                .signWith(signingKey)
                .compact();

        when(redisTemplate.hasKey("blacklist:blacklisted-jti")).thenReturn(true);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/perros")
                .header("Authorization", "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verifyNoInteractions(filterChain);
    }
}