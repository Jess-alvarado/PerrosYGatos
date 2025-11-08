package com.owner.pyg_owner.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.owner.pyg_owner.clients.AuthServiceClient;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthServiceClient authServiceClient;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String incomingAuthHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Enforce presence of Authorization header (service protegido)
        if (!StringUtils.hasText(incomingAuthHeader)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization header missing");
            return;
        }

        try {
            // Normalizar header: aceptar tanto "Bearer <token>" como "<token>"
            String normalizedHeader = incomingAuthHeader.trim();

            if (!normalizedHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
                normalizedHeader = "Bearer " + normalizedHeader;
            }

            // Validación centralizada en pyg-auth. Pasamos el header normalizado.
            var validation = authServiceClient.validateToken(normalizedHeader);

            if (validation == null || !validation.isValid()) {
                log.warn("Token validation failed");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Invalid or expired token");
                return;
            }

            log.debug("Token validated successfully for user: {}", validation.getUsername());

            // Crear autoridades de Spring Security a partir del rol validado
            var authorities = List
                    .of(new org.springframework.security.core.authority.SimpleGrantedAuthority(validation.getRole()));

            // Crear token de autenticación para Spring Security
            var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    validation.getUsername(),
                    null,
                    authorities);

            // Establecer detalles adicionales
            authToken.setDetails(validation);

            // Establecer el contexto de seguridad
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authToken);

            // Opcional: compartir info con capas posteriores
            request.setAttribute("auth.userId", validation.getUserId());
            request.setAttribute("auth.username", validation.getUsername());
            request.setAttribute("auth.role", validation.getRole());

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Token validation error: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Authentication service unavailable or token invalid");
        }
    }
}
