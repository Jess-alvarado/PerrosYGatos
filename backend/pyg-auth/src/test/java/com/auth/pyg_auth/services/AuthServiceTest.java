package com.auth.pyg_auth.services;

import com.auth.pyg_auth.dto.requests.LoginRequest;
import com.auth.pyg_auth.dto.requests.RefreshTokenRequest;
import com.auth.pyg_auth.dto.requests.UserRegisterRequest;
import com.auth.pyg_auth.dto.responses.AuthResponse;
import com.auth.pyg_auth.models.RefreshToken;
import com.auth.pyg_auth.models.Role;
import com.auth.pyg_auth.models.User;
import com.auth.pyg_auth.repositories.RoleRepository;
import com.auth.pyg_auth.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AccessTokenBlacklistService accessTokenBlacklistService;

    @InjectMocks
    private AuthService authService;

    private User usuarioPrueba;
    private Role rolPrueba;
    private RefreshToken refreshTokenPrueba;

    @BeforeEach
    void setUp() {
        rolPrueba = Role.builder()
                .id(1L)
                .name("ROLE_OWNER")
                .build();

        usuarioPrueba = User.builder()
                .id(1L)
                .username("ana@perrosgatos.cl")
                .password("password_encriptado")
                .firstname("Ana")
                .lastname("González")
                .role(rolPrueba)
                .build();

        refreshTokenPrueba = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-uuid-prueba")
                .user(usuarioPrueba)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }

    @Test
    @DisplayName("Successful login returns access token and refresh token")
    void login_conCredencialesValidas_debeRetornarAuthResponse() {
        LoginRequest request = LoginRequest.builder()
                .username("ana@perrosgatos.cl")
                .password("password123")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(userRepository.findByUsername("ana@perrosgatos.cl"))
                .thenReturn(Optional.of(usuarioPrueba));
        when(jwtService.generateAccessToken(usuarioPrueba))
                .thenReturn("access-token-generado");
        when(refreshTokenService.createRefreshToken(usuarioPrueba))
                .thenReturn(refreshTokenPrueba);

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token-generado");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-uuid-prueba");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Login with non-existent user throws exception")
    void login_conUsuarioNoExistente_debeLanzarExcepcion() {
        LoginRequest request = LoginRequest.builder()
                .username("noexiste@perrosgatos.cl")
                .password("cualquiera")
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("noexiste@perrosgatos.cl"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Login with invalid credentials throws BadCredentialsException")
    void login_conCredencialesInvalidas_debeLanzarBadCredentialsException() {
        LoginRequest request = LoginRequest.builder()
                .username("ana@perrosgatos.cl")
                .password("password_incorrecto")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Successful registration saves user and returns tokens")
    void register_conDatosValidos_debeCrearUsuarioYRetornarTokens() {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("nuevo@perrosgatos.cl")
                .password("password123")
                .firstname("Pedro")
                .lastname("Soto")
                .rolename("ROLE_PROFESSIONAL")
                .build();

        Role rolProfesional = Role.builder().id(2L).name("ROLE_PROFESSIONAL").build();

        when(roleRepository.findByName("ROLE_PROFESSIONAL"))
                .thenReturn(Optional.of(rolProfesional));
        when(passwordEncoder.encode("password123"))
                .thenReturn("password_encriptado_bcrypt");
        when(userRepository.save(any(User.class)))
                .thenReturn(usuarioPrueba);
        when(jwtService.generateAccessToken(any(User.class)))
                .thenReturn("nuevo-access-token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(refreshTokenPrueba);

        AuthResponse response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("nuevo-access-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Registration with non-existent role throws exception")
    void register_conRolInexistente_debeLanzarExcepcion() {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("nuevo@perrosgatos.cl")
                .password("password123")
                .firstname("Pedro")
                .lastname("Soto")
                .rolename("ROLE_INEXISTENTE")
                .build();

        when(roleRepository.findByName("ROLE_INEXISTENTE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Valid refresh token rotates tokens correctly")
    void refresh_conRefreshTokenValido_debeRotarTokens() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refresh-token-uuid-prueba")
                .build();

        RefreshToken nuevoRefreshToken = RefreshToken.builder()
                .id(2L)
                .token("nuevo-refresh-token-uuid")
                .user(usuarioPrueba)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        when(refreshTokenService.validateRefreshToken("refresh-token-uuid-prueba"))
                .thenReturn(refreshTokenPrueba);
        when(jwtService.generateAccessToken(usuarioPrueba))
                .thenReturn("nuevo-access-token");
        when(refreshTokenService.createRefreshToken(usuarioPrueba))
                .thenReturn(nuevoRefreshToken);

        AuthResponse response = authService.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo("nuevo-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("nuevo-refresh-token-uuid");

        verify(refreshTokenService, times(1)).revokeToken("refresh-token-uuid-prueba");
    }

    @Test
    @DisplayName("Logout revokes access token and all refresh tokens for the user")
    void logout_debeRevocarAccessTokenYRefreshTokens() {
        String accessToken = "access-token-a-revocar";

        when(jwtService.getUserIdFromToken(accessToken)).thenReturn(1L);
        when(jwtService.getExpirationDateFromToken(accessToken))
                .thenReturn(new java.util.Date(System.currentTimeMillis() + 3600000L));

        authService.logout(accessToken);

        verify(accessTokenBlacklistService, times(1))
                .blacklistToken(eq(accessToken), any());
        verify(refreshTokenService, times(1))
                .revokeAllUserTokens(1L);
    }
}