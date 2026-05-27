package com.auth.pyg_auth.services;

import com.auth.pyg_auth.models.RefreshToken;
import com.auth.pyg_auth.models.Role;
import com.auth.pyg_auth.models.User;
import com.auth.pyg_auth.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private static final Long REFRESH_EXPIRATION_MS = 604800000L;

    private User usuarioPrueba;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                refreshTokenService, "refreshTokenExpirationMs", REFRESH_EXPIRATION_MS
        );

        Role rol = Role.builder().id(1L).name("ROLE_OWNER").build();

        usuarioPrueba = User.builder()
                .id(1L)
                .username("ana@perrosgatos.cl")
                .password("password_encriptado")
                .firstname("Ana")
                .lastname("González")
                .role(rol)
                .build();
    }

    @Test
    @DisplayName("Should create refresh token with correct user data")
    void createRefreshToken_debeCrearTokenConDatosCorrectos() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken resultado = refreshTokenService.createRefreshToken(usuarioPrueba);

        assertThat(resultado.getUser()).isEqualTo(usuarioPrueba);
        assertThat(resultado.isRevoked()).isFalse();
        assertThat(resultado.getToken()).isNotBlank();
        assertThat(resultado.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should generate a unique UUID token for every request")
    void createRefreshToken_debeGenerarTokenUnico() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token1 = refreshTokenService.createRefreshToken(usuarioPrueba);
        RefreshToken token2 = refreshTokenService.createRefreshToken(usuarioPrueba);

        assertThat(token1.getToken()).isNotEqualTo(token2.getToken());
    }

    @Test
    @DisplayName("Should set token expiration time to approximately 7 days")
    void createRefreshToken_debeExpirarEnElTiempoCorrect() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken resultado = refreshTokenService.createRefreshToken(usuarioPrueba);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime expiracionEsperada = ahora.plusSeconds(REFRESH_EXPIRATION_MS / 1000);

        assertThat(resultado.getExpiresAt())
                .isAfter(ahora.plusDays(6))
                .isBefore(expiracionEsperada.plusSeconds(5));
    }

    @Test
    @DisplayName("Should return the token when it is valid and active")
    void validateRefreshToken_conTokenValido_debeRetornarToken() {
        RefreshToken tokenValido = RefreshToken.builder()
                .token("uuid-valido")
                .user(usuarioPrueba)
                .expiresAt(LocalDateTime.now().plusDays(3))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenAndRevokedFalse("uuid-valido"))
                .thenReturn(Optional.of(tokenValido));

        RefreshToken resultado = refreshTokenService.validateRefreshToken("uuid-valido");

        assertThat(resultado.getToken()).isEqualTo("uuid-valido");
    }

    @Test
    @DisplayName("Should throw exception when token is not found or revoked")
    void validateRefreshToken_conTokenNoExistente_debeLanzarExcepcion() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse("token-inexistente"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                refreshTokenService.validateRefreshToken("token-inexistente"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token not found or revoked");
    }

    @Test
    @DisplayName("Should throw exception when token exists but is expired")
    void validateRefreshToken_conTokenExpirado_debeLanzarExcepcion() {
        RefreshToken tokenExpirado = RefreshToken.builder()
                .token("uuid-expirado")
                .user(usuarioPrueba)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenAndRevokedFalse("uuid-expirado"))
                .thenReturn(Optional.of(tokenExpirado));

        assertThatThrownBy(() ->
                refreshTokenService.validateRefreshToken("uuid-expirado"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token expired");
    }

    @Test
    @DisplayName("Should mark token as revoked and save it when revoking an active token")
    void revokeToken_conTokenExistente_debeMarcarlo() {
        RefreshToken tokenActivo = RefreshToken.builder()
                .token("uuid-activo")
                .user(usuarioPrueba)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenAndRevokedFalse("uuid-activo"))
                .thenReturn(Optional.of(tokenActivo));

        refreshTokenService.revokeToken("uuid-activo");

        assertThat(tokenActivo.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(1)).save(tokenActivo);
    }

    @Test
    @DisplayName("Should throw exception when trying to revoke a non-existent token")
    void revokeToken_conTokenNoExistente_debeLanzarExcepcion() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse("no-existe"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.revokeToken("no-existe"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token not found or already revoked");
    }

    @Test
    @DisplayName("Should mark and save all active tokens for a user as revoked")
    void revokeAllUserTokens_debeMarcarTodosComoRevocados() {
        RefreshToken token1 = RefreshToken.builder()
                .token("uuid-1").user(usuarioPrueba)
                .expiresAt(LocalDateTime.now().plusDays(7)).revoked(false).build();
        RefreshToken token2 = RefreshToken.builder()
                .token("uuid-2").user(usuarioPrueba)
                .expiresAt(LocalDateTime.now().plusDays(5)).revoked(false).build();

        when(refreshTokenRepository.findAllByUserIdAndRevokedFalse(1L))
                .thenReturn(List.of(token1, token2));

        refreshTokenService.revokeAllUserTokens(1L);

        assertThat(token1.isRevoked()).isTrue();
        assertThat(token2.isRevoked()).isTrue();

        ArgumentCaptor<List<RefreshToken>> captor = ArgumentCaptor.forClass(List.class);
        verify(refreshTokenRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    @DisplayName("Should call saveAll with an empty list if user has no active tokens")
    void revokeAllUserTokens_sinTokensActivos_noDebeGuardarNada() {
        when(refreshTokenRepository.findAllByUserIdAndRevokedFalse(99L))
                .thenReturn(List.of());

        refreshTokenService.revokeAllUserTokens(99L);

        verify(refreshTokenRepository, times(1)).saveAll(List.of());
    }
}