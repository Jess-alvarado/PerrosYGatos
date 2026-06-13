package com.professional.pyg_professional.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtSignatureFilterTest {

    @InjectMocks
    private JwtSignatureFilter jwtSignatureFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private String testSecretKey;
    private Key signingKey;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        testSecretKey = Base64.getEncoder().encodeToString(signingKey.getEncoded());

        ReflectionTestUtils.setField(jwtSignatureFilter, "secretKey", testSecretKey);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should bypass authentication when path starts with public endpoints like /actuator")
    void doFilter_PublicPath_ShouldBypassAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/actuator/health");

        jwtSignatureFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should proceed with filter chain and AUTHENTICATE professional when token signature is valid")
    void doFilter_ValidToken_ShouldProceedWithFilterChain() throws Exception {
        when(request.getRequestURI()).thenReturn("/professionals/profile");

        String validToken = Jwts.builder()
                .setSubject("testProfessional")
                .claim("uid", 456L)
                .claim("role", "ROLE_PROFESSIONAL")
                .signWith(signingKey)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        jwtSignatureFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testProfessional", SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSIONAL")));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when Authorization header is missing")
    void doFilter_MissingHeader_ShouldReturn401Unauthorized() throws Exception {
        when(request.getRequestURI()).thenReturn("/professionals/profile");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtSignatureFilter.doFilter(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when token is signed with an invalid secret key")
    void doFilter_InvalidSignature_ShouldReturn401Unauthorized() throws Exception {
        when(request.getRequestURI()).thenReturn("/professionals/profile");

        Key wrongKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String invalidToken = Jwts.builder()
                .setSubject("unauthorizedProfessional")
                .signWith(wrongKey)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);

        jwtSignatureFilter.doFilter(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}