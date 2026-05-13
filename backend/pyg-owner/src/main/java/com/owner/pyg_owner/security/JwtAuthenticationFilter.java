package com.owner.pyg_owner.security;

import com.owner.pyg_owner.clients.AuthServiceClient;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthServiceClient authServiceClient;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources/")
                || path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader)) {

            log.warn("Missing Authorization header");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization header missing");

            return;
        }

        String normalizedHeader = authHeader.trim();

        if (!normalizedHeader.regionMatches(true, 0,
                "Bearer ", 0, 7)) {

            normalizedHeader = "Bearer " + normalizedHeader;
        }

        try {

            var validation = authServiceClient.validateToken(normalizedHeader);

            if (validation == null || !validation.isValid()) {

                log.warn("Invalid or expired token");

                SecurityContextHolder.clearContext();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");

                return;
            }

            var authorities = List.of(
                    new SimpleGrantedAuthority(validation.getRole()));

            var authentication = new UsernamePasswordAuthenticationToken(
                    validation.getUsername(),
                    null,
                    authorities);

            authentication.setDetails(validation);

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            request.setAttribute("auth.userId",
                    validation.getUserId());

            request.setAttribute("auth.username",
                    validation.getUsername());

            request.setAttribute("auth.role",
                    validation.getRole());

            filterChain.doFilter(request, response);

        } catch (FeignException.Unauthorized ex) {

            log.error("Unauthorized token", ex);

            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");

        } catch (FeignException.ServiceUnavailable ex) {

            log.error("Auth service unavailable", ex);

            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter()
                    .write("Authentication service unavailable");

        } catch (FeignException ex) {

            log.error("Feign auth error", ex);

            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter()
                    .write("Authentication error");
        }
    }
}