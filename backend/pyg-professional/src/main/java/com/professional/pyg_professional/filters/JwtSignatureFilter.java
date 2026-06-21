package com.professional.pyg_professional.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
@Order(2) // después del TraceIdFilter
public class JwtSignatureFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final String[] PUBLIC_PATHS = {
            "/actuator"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = authHeader.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            if (username != null) {
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                if (role != null) {
                    authorities = List.of(new SimpleGrantedAuthority(role));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
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