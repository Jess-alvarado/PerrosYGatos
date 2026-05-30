package com.professional.pyg_professional.security;

import com.professional.pyg_professional.clients.AuthServiceClient;
import com.professional.pyg_professional.dto.responses.TokenValidationResponse;
import feign.FeignException;
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

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() {
        responseWriter = new StringWriter();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Swagger UI path is excluded from filter")
    void shouldNotFilter_swaggerPath_shouldReturnTrue() {
        when(request.getServletPath()).thenReturn("/swagger-ui/index.html");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    @DisplayName("API docs path is excluded from filter")
    void shouldNotFilter_apiDocsPath_shouldReturnTrue() {
        when(request.getServletPath()).thenReturn("/v3/api-docs/swagger-config");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    @DisplayName("Protected endpoint is not excluded from filter")
    void shouldNotFilter_protectedPath_shouldReturnFalse() {
        when(request.getServletPath()).thenReturn("/professionals/profile");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    @Test
    @DisplayName("Returns 401 when Authorization header is missing")
    void doFilterInternal_withMissingHeader_shouldReturn401() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        // Lo configuramos localmente solo para este test que sí lo usa:
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(responseWriter.toString()).contains("Authorization header missing");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Returns 401 when Authorization header is blank")
    void doFilterInternal_withBlankHeader_shouldReturn401() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("   ");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Adds Bearer prefix when header has raw token without it")
    void doFilterInternal_withRawTokenNoBearerPrefix_shouldNormalize() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("raw-token-without-bearer");

        TokenValidationResponse validResponse = TokenValidationResponse.builder()
                .userId(1L).username("jess.alvarado")
                .role("ROLE_PROFESSIONAL").valid(true)
                .expiresAt(System.currentTimeMillis() + 3600000L)
                .build();

        when(authServiceClient.validateToken("Bearer raw-token-without-bearer"))
                .thenReturn(validResponse);

        filter.doFilterInternal(request, response, filterChain);

        verify(authServiceClient).validateToken("Bearer raw-token-without-bearer");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Sets SecurityContext and continues filter chain for valid token")
    void doFilterInternal_withValidToken_shouldSetSecurityContextAndContinue() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

        TokenValidationResponse validResponse = TokenValidationResponse.builder()
                .userId(1L).username("jess.alvarado")
                .role("ROLE_PROFESSIONAL").valid(true)
                .expiresAt(System.currentTimeMillis() + 3600000L)
                .build();

        when(authServiceClient.validateToken("Bearer valid-token")).thenReturn(validResponse);

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("jess.alvarado");
        assertThat(auth.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSIONAL"));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Sets request attributes with user data for valid token")
    void doFilterInternal_withValidToken_shouldSetRequestAttributes() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

        TokenValidationResponse validResponse = TokenValidationResponse.builder()
                .userId(1L).username("jess.alvarado")
                .role("ROLE_PROFESSIONAL").valid(true)
                .expiresAt(System.currentTimeMillis() + 3600000L)
                .build();

        when(authServiceClient.validateToken("Bearer valid-token")).thenReturn(validResponse);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute("auth.userId", 1L);
        verify(request).setAttribute("auth.username", "jess.alvarado");
        verify(request).setAttribute("auth.role", "ROLE_PROFESSIONAL");
    }

    @Test
    @DisplayName("Returns 401 when token validation returns invalid response")
    void doFilterInternal_withInvalidTokenResponse_shouldReturn401() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        TokenValidationResponse invalidResponse = TokenValidationResponse.builder()
                .valid(false).build();

        when(authServiceClient.validateToken(anyString())).thenReturn(invalidResponse);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(responseWriter.toString()).contains("Invalid or expired token");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Returns 401 when auth service returns null")
    void doFilterInternal_withNullValidationResponse_shouldReturn401() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer some-token");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(authServiceClient.validateToken(anyString())).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Returns 401 on FeignException.Unauthorized")
    void doFilterInternal_onFeignUnauthorized_shouldReturn401() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        when(authServiceClient.validateToken(anyString()))
                .thenThrow(mock(FeignException.Unauthorized.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Returns 503 on FeignException.ServiceUnavailable")
    void doFilterInternal_onFeignServiceUnavailable_shouldReturn503() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer some-token");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        when(authServiceClient.validateToken(anyString()))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        assertThat(responseWriter.toString()).contains("Authentication service unavailable");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Returns 403 on generic FeignException")
    void doFilterInternal_onGenericFeignException_shouldReturn403() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer some-token");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        when(authServiceClient.validateToken(anyString()))
                .thenThrow(mock(FeignException.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertThat(responseWriter.toString()).contains("Authentication error");
        verify(filterChain, never()).doFilter(any(), any());
    }
}