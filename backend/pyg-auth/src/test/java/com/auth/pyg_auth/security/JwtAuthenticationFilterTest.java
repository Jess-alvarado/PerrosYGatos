package com.auth.pyg_auth.security;

import com.auth.pyg_auth.services.AccessTokenBlacklistService;
import com.auth.pyg_auth.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("shouldNotFilter - Should return true for configured public paths")
    void shouldNotFilter_ConRutasPublicas_DebeRetornarTrue() {
        String[] rutasPublicas = {
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/refresh",
                "/api/auth/validate",
                "/v3/api-docs",
                "/swagger-ui"
        };

        for (String ruta : rutasPublicas) {
            request.setServletPath(ruta);
            boolean resultado = jwtAuthenticationFilter.shouldNotFilter(request);
            assertThat(resultado).as("Path %s should bypass the filter", ruta).isTrue();
        }
    }

    @Test
    @DisplayName("doFilterInternal - Should continue the chain if Authorization header is missing")
    void doFilterInternal_SinHeaderAuthorization_ContinuaLaCadena() throws ServletException, IOException {
        request.setServletPath("/api/perros/lista");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtService, accessTokenBlacklistService, userDetailsService);
    }

    @Test
    @DisplayName("doFilterInternal - Should return 401 if token is blacklisted")
    void doFilterInternal_ConTokenEnListaNegra_Retorna401() throws ServletException, IOException {
        String tokenPrueba = "token-revocado-123";
        request.setServletPath("/api/perros/lista");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPrueba);

        when(accessTokenBlacklistService.isBlacklisted(tokenPrueba)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains("Access token has been revoked");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("doFilterInternal - Should authenticate user successfully when token is valid")
    void doFilterInternal_ConTokenValido_AutenticaAlUsuario() throws ServletException, IOException {
        String tokenPrueba = "token-valido-xyz";
        String usernamePrueba = "ana@perrosgatos.cl";

        request.setServletPath("/api/perros/lista");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPrueba);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());

        when(accessTokenBlacklistService.isBlacklisted(tokenPrueba)).thenReturn(false);
        when(jwtService.getUsernameFromToken(tokenPrueba)).thenReturn(usernamePrueba);
        when(userDetailsService.loadUserByUsername(usernamePrueba)).thenReturn(mockUserDetails);
        when(jwtService.isTokenValid(tokenPrueba, mockUserDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(mockUserDetails);
    }

    @Test
    @DisplayName("doFilterInternal - Should return 401 when an unexpected exception occurs")
    void doFilterInternal_CuandoOcurreExcepcion_Retorna401ConMensajeDeInvalido() throws ServletException, IOException {
        String tokenCorrupto = "token-roto";
        request.setServletPath("/api/perros/lista");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenCorrupto);

        when(accessTokenBlacklistService.isBlacklisted(tokenCorrupto)).thenReturn(false);
        when(jwtService.getUsernameFromToken(tokenCorrupto)).thenThrow(new RuntimeException("JWT String argument cannot be null or empty."));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains("Invalid or expired token");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}