package com.owner.pyg_owner.security;

import com.owner.pyg_owner.clients.AuthServiceClient;
import com.owner.pyg_owner.dto.responses.TokenValidationResponse;
import feign.FeignException;
import feign.Request;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return 401 when Authorization header is missing")
    void shouldReturn401WhenAuthorizationHeaderMissing() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain);

        assertEquals(401, response.getStatus());
        assertEquals(
                "Authorization header missing",
                response.getContentAsString());

        verifyNoInteractions(authServiceClient);
    }

    @Test
    @DisplayName("Should authenticate successfully with valid token")
    void shouldAuthenticateSuccessfullyWithValidToken()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer valid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        TokenValidationResponse validation =
                TokenValidationResponse.builder()
                        .valid(true)
                        .userId(1L)
                        .username("jess")
                        .role("ROLE_OWNER")
                        .build();

        when(authServiceClient.validateToken(anyString()))
                .thenReturn(validation);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain);

        assertNotNull(
                SecurityContextHolder.getContext()
                        .getAuthentication());

        assertEquals(
                "jess",
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal());

        assertEquals(1L,
                request.getAttribute("auth.userId"));

        verify(filterChain, times(1))
                .doFilter(request, response);
    }

    @Test
    @DisplayName("Should normalize token without Bearer prefix")
    void shouldNormalizeTokenWithoutBearerPrefix()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "plain-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        TokenValidationResponse validation =
                TokenValidationResponse.builder()
                        .valid(true)
                        .userId(1L)
                        .username("jess")
                        .role("ROLE_OWNER")
                        .build();

        when(authServiceClient.validateToken(anyString()))
                .thenReturn(validation);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(authServiceClient, times(1))
                .validateToken("Bearer plain-token");
    }

    @Test
    @DisplayName("Should return 401 when token is invalid")
    void shouldReturn401WhenTokenInvalid()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer invalid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        TokenValidationResponse validation =
                TokenValidationResponse.builder()
                        .valid(false)
                        .build();

        when(authServiceClient.validateToken(anyString()))
                .thenReturn(validation);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain);

        assertEquals(401, response.getStatus());

        assertEquals(
                "Invalid or expired token",
                response.getContentAsString());
    }

    @Test
    @DisplayName("Should return 401 when auth service returns unauthorized")
    void shouldReturn401WhenUnauthorized()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer invalid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        Request feignRequest = Request.create(
                Request.HttpMethod.POST,
                "/validate",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null);

        when(authServiceClient.validateToken(anyString()))
                .thenThrow(
                        new FeignException.Unauthorized(
                                "Unauthorized",
                                feignRequest,
                                null,
                                null));

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain);

        assertEquals(401, response.getStatus());

        assertEquals(
                "Invalid or expired token",
                response.getContentAsString());
    }

    @Test
    @DisplayName("Should return 503 when auth service is unavailable")
    void shouldReturn503WhenServiceUnavailable()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        Request feignRequest = Request.create(
                Request.HttpMethod.POST,
                "/validate",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null);

        when(authServiceClient.validateToken(anyString()))
                .thenThrow(
                        new FeignException.ServiceUnavailable(
                                "Service unavailable",
                                feignRequest,
                                null,
                                null));

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain);

        assertEquals(503, response.getStatus());

        assertEquals(
                "Authentication service unavailable",
                response.getContentAsString());
    }

    @Test
    @DisplayName("Should return 403 on generic feign error")
    void shouldReturn403OnGenericFeignError()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        Request feignRequest = Request.create(
                Request.HttpMethod.POST,
                "/validate",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null);

        when(authServiceClient.validateToken(anyString()))
                .thenThrow(
                        new FeignException.Forbidden(
                                "Forbidden",
                                feignRequest,
                                null,
                                null));

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain);

        assertEquals(403, response.getStatus());

        assertEquals(
                "Authentication error",
                response.getContentAsString());
    }
}