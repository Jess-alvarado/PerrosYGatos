package com.auth.pyg_auth.filters;

import com.auth.pyg_auth.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should bypass the filter and continue chain when path is public")
    void doFilter_withPublicPath_shouldContinueChainWithoutCheckingToken() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when Authorization header is missing")
    void doFilter_withMissingHeader_shouldReturn401() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/validate");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when header does not start with Bearer")
    void doFilter_withInvalidHeaderPrefix_shouldReturn401() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/validate");
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should authenticate user and continue chain when token is valid")
    void doFilter_withValidToken_shouldAuthenticateAndContinueChain() throws Exception {
        String token = "valid-jwt-token-string";
        Claims claims = new DefaultClaims();
        claims.setSubject("jess@perrosygatos.cl");
        claims.put("role", "ROLE_OWNER");

        when(request.getRequestURI()).thenReturn("/api/auth/validate");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getAllClaims(token)).thenReturn(claims);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("jess@perrosygatos.cl");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_OWNER");
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when JwtService throws an exception")
    void doFilter_whenTokenParsingFails_shouldReturn401() throws Exception {
        String expiredToken = "expired-token";
        when(request.getRequestURI()).thenReturn("/api/auth/validate");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtService.getAllClaims(expiredToken)).thenThrow(new RuntimeException("JWT Expired"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}