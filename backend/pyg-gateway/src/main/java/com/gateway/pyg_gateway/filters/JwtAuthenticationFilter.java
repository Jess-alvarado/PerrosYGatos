package com.gateway.pyg_gateway.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secretKey;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private static final String[] PUBLIC_PATHS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/actuator"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return chain.filter(exchange);
            }
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String jti = claims.get("jti", String.class);
            if (jti != null && Boolean.TRUE.equals(
                    redisTemplate.hasKey(BLACKLIST_PREFIX + jti))) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Id",
                                    String.valueOf(claims.get("uid", Long.class)))
                            .header("X-User-Role",
                                    claims.get("role", String.class))
                            .header("X-Username",
                                    claims.getSubject())
                            .build())
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Key getKey() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        } catch (Exception e) {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            String base64Key = Base64.getEncoder().encodeToString(keyBytes);
            return Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
        }
    }
}