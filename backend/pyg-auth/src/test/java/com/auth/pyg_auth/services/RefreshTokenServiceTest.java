package com.auth.pyg_auth.services;

import com.auth.pyg_auth.exceptions.InvalidCredentialsException;
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

    private static final Long REFRESH_EXPIRATION_MS = 604800000L; // 7 days

    private User mockUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                refreshTokenService, "refreshTokenExpirationMs", REFRESH_EXPIRATION_MS
        );

        Role role = Role.builder().id(1L).name("ROLE_OWNER").build();

        mockUser = User.builder()
                .id(1L)
                .username("jess@perrosygatos.cl")
                .password("encoded_password")
                .firstname("Jess")
                .lastname("Alvarado")
                .role(role)
                .build();
    }

    @Test
    @DisplayName("Should create refresh token with correct user data")
    void createRefreshToken_withValidUser_shouldCreateTokenWithCorrectData() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(mockUser);

        assertThat(result.getUser()).isEqualTo(mockUser);
        assertThat(result.isRevoked()).isFalse();
        assertThat(result.getToken()).isNotBlank();
        assertThat(result.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should generate a unique UUID token for every request")
    void createRefreshToken_shouldGenerateUniqueTokens() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token1 = refreshTokenService.createRefreshToken(mockUser);
        RefreshToken token2 = refreshTokenService.createRefreshToken(mockUser);

        assertThat(token1.getToken()).isNotEqualTo(token2.getToken());
    }

    @Test
    @DisplayName("Should set token expiration time to approximately 7 days")
    void createRefreshToken_shouldHaveCorrectExpirationTime() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(mockUser);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedExpiration = now.plusSeconds(REFRESH_EXPIRATION_MS / 1000);

        assertThat(result.getExpiresAt())
                .isAfter(now.plusDays(6))
                .isBefore(expectedExpiration.plusSeconds(5));
    }

    @Test
    @DisplayName("Should return the token when it is valid and active")
    void validateRefreshToken_withValidToken_shouldReturnToken() {
        RefreshToken validToken = RefreshToken.builder()
                .token("valid-uuid")
                .user(mockUser)
                .expiresAt(LocalDateTime.now().plusDays(3))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenAndRevokedFalse("valid-uuid"))
                .thenReturn(Optional.of(validToken));

        RefreshToken result = refreshTokenService.validateRefreshToken("valid-uuid");

        assertThat(result.getToken()).isEqualTo("valid-uuid");
    }

    @Test
    @DisplayName("Should throw exception when token is not found or revoked")
    void validateRefreshToken_withNonExistentToken_shouldThrowInvalidCredentialsException() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse("missing-token"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                refreshTokenService.validateRefreshToken("missing-token"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    @DisplayName("Should throw exception when token exists but is expired")
    void validateRefreshToken_withExpiredToken_shouldThrowInvalidCredentialsException() {
        RefreshToken expiredToken = RefreshToken.builder()
                .token("expired-uuid")
                .user(mockUser)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenAndRevokedFalse("expired-uuid"))
                .thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() ->
                refreshTokenService.validateRefreshToken("expired-uuid"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    @DisplayName("Should mark token as revoked and save it when revoking an active token")
    void revokeToken_withActiveToken_shouldMarkAsRevokedAndSave() {
        RefreshToken activeToken = RefreshToken.builder()
                .token("active-uuid")
                .user(mockUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenAndRevokedFalse("active-uuid"))
                .thenReturn(Optional.of(activeToken));

        refreshTokenService.revokeToken("active-uuid");

        assertThat(activeToken.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(1)).save(activeToken);
    }

    @Test
    @DisplayName("Should throw exception when trying to revoke a non-existent token")
    void revokeToken_withNonExistentToken_shouldThrowInvalidCredentialsException() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse("non-existent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.revokeToken("non-existent"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    @DisplayName("Should mark and save all active tokens for a user as revoked")
    void revokeAllUserTokens_shouldRevokeAndSaveAllTokens() {
        RefreshToken token1 = RefreshToken.builder()
                .token("uuid-1").user(mockUser)
                .expiresAt(LocalDateTime.now().plusDays(7)).revoked(false).build();
        RefreshToken token2 = RefreshToken.builder()
                .token("uuid-2").user(mockUser)
                .expiresAt(LocalDateTime.now().plusDays(5)).revoked(false).build();

        when(refreshTokenRepository.findAllByUserIdAndRevokedFalse(1L))
                .thenReturn(List.of(token1, token2));

        refreshTokenService.revokeAllUserTokens(1L);

        assertThat(token1.isRevoked()).isTrue();
        assertThat(token2.isRevoked()).isTrue();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RefreshToken>> captor = ArgumentCaptor.forClass(List.class);
        verify(refreshTokenRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    @DisplayName("Should NOT call saveAll if user has no active tokens")
    void revokeAllUserTokens_withNoActiveTokens_shouldNotSaveAnything() {
        when(refreshTokenRepository.findAllByUserIdAndRevokedFalse(99L))
                .thenReturn(List.of());

        refreshTokenService.revokeAllUserTokens(99L);

        verify(refreshTokenRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when database fails during mass revocation")
    void revokeAllUserTokens_whenRepositoryFails_shouldThrowInvalidCredentialsException() {
        when(refreshTokenRepository.findAllByUserIdAndRevokedFalse(1L))
                .thenThrow(new RuntimeException("Database connection down"));

        assertThatThrownBy(() -> refreshTokenService.revokeAllUserTokens(1L))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Error processing session termination");
    }
}