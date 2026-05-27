package com.auth.pyg_auth.services;

import com.auth.pyg_auth.models.BlacklistedAccessToken;
import com.auth.pyg_auth.repositories.BlacklistedAccessTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlacklistServiceTest {

    @Mock
    private BlacklistedAccessTokenRepository blacklistedRepo;

    @InjectMocks
    private AccessTokenBlacklistService blacklistService;

    private static final String TOKEN_PRUEBA = "eyJhbGciOiJIUzI1NiJ9.prueba.firma";
    private Date fechaExpiracion;

    @BeforeEach
    void setUp() {
        fechaExpiracion = new Date(System.currentTimeMillis() + 3600000L);
    }

    @Test
    @DisplayName("Should save new token successfully to the blacklist")
    void blacklistToken_conTokenNuevo_debeGuardarEnRepositorio() {
        when(blacklistedRepo.existsByToken(TOKEN_PRUEBA)).thenReturn(false);

        blacklistService.blacklistToken(TOKEN_PRUEBA, fechaExpiracion);

        verify(blacklistedRepo, times(1)).save(any(BlacklistedAccessToken.class));
    }

    @Test
    @DisplayName("Should not save duplicate token if it already exists")
    void blacklistToken_conTokenYaExistente_noDebeGuardarDenuevo() {
        when(blacklistedRepo.existsByToken(TOKEN_PRUEBA)).thenReturn(true);

        blacklistService.blacklistToken(TOKEN_PRUEBA, fechaExpiracion);

        verify(blacklistedRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should save token with correct data fields")
    void blacklistToken_debeGuardarTokenConDatosCorrectos() {
        when(blacklistedRepo.existsByToken(TOKEN_PRUEBA)).thenReturn(false);

        ArgumentCaptor<BlacklistedAccessToken> captor = ArgumentCaptor.forClass(BlacklistedAccessToken.class);

        blacklistService.blacklistToken(TOKEN_PRUEBA, fechaExpiracion);

        verify(blacklistedRepo).save(captor.capture());
        BlacklistedAccessToken tokenGuardado = captor.getValue();

        assertThat(tokenGuardado.getToken()).isEqualTo(TOKEN_PRUEBA);
        assertThat(tokenGuardado.getExpiresAt()).isNotNull();

        LocalDateTime esperado = fechaExpiracion.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        assertThat(tokenGuardado.getExpiresAt()).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Should return true when token is blacklisted")
    void isBlacklisted_conTokenEnLista_debeRetornarTrue() {
        when(blacklistedRepo.existsByToken(TOKEN_PRUEBA)).thenReturn(true);

        boolean resultado = blacklistService.isBlacklisted(TOKEN_PRUEBA);

        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Should return false when token is not blacklisted")
    void isBlacklisted_conTokenNoEnLista_debeRetornarFalse() {
        when(blacklistedRepo.existsByToken(TOKEN_PRUEBA)).thenReturn(false);

        boolean resultado = blacklistService.isBlacklisted(TOKEN_PRUEBA);

        assertThat(resultado).isFalse();
    }
}