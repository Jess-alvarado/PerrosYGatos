package com.auth.pyg_auth.services;

import com.auth.pyg_auth.exceptions.InvalidCredentialsException;
import com.auth.pyg_auth.models.Role;
import com.auth.pyg_auth.models.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User mockUser;

    private static final String SECRET_TEST = "dGVzdFNlY3JldEtleVBhcmFQcnVlYmFzUHlnQXV0aDEyMzQ1Njc4OTA=";
    private static final Long EXPIRATION_MS = 3600000L; // 1 h

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_TEST);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", EXPIRATION_MS);

        Role role = new Role();
        role.setName("ROLE_OWNER");

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
    @DisplayName("Should generate token with all correct custom and standard claims")
    void generateAccessToken_shouldContainCorrectClaims() {
        String token = jwtService.generateAccessToken(mockUser);
        Claims claims = jwtService.getAllClaims(token);

        assertThat(claims.getSubject()).isEqualTo("jess@perrosygatos.cl");
        assertThat(claims.get("uid", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_OWNER");
        assertThat(claims.get("firstname", String.class)).isEqualTo("Jess");
        assertThat(claims.get("lastname", String.class)).isEqualTo("Alvarado");
        assertThat(claims.get("jti", String.class)).isNotBlank();
    }

    @Test
    @DisplayName("Generated token should have a future expiration date")
    void generateAccessToken_shouldHaveFutureExpiration() {
        String token = jwtService.generateAccessToken(mockUser);
        Date expiration = jwtService.getExpirationDateFromToken(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Valid token should be recognized as valid")
    void isTokenValid_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateAccessToken(mockUser);
        boolean isValid = jwtService.isTokenValid(token, mockUser);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Token from another user should be invalid for current user")
    void isTokenValid_withTokenFromAnotherUser_shouldReturnFalse() {
        Role professionalRole = new Role();
        professionalRole.setName("ROLE_PROFESSIONAL");

        User anotherUser = User.builder()
                .id(2L)
                .username("alex@perrosygatos.cl")
                .password("another_password")
                .firstname("Alex")
                .lastname("Smith")
                .role(professionalRole)
                .build();

        String tokenFromAnotherUser = jwtService.generateAccessToken(anotherUser);
        boolean isValid = jwtService.isTokenValid(tokenFromAnotherUser, mockUser);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Expired token should throw InvalidCredentialsException on validation")
    void isTokenValid_withExpiredToken_shouldThrowInvalidCredentialsException() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1000L);
        String expiredToken = jwtService.generateAccessToken(mockUser);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", EXPIRATION_MS);

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, mockUser))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid, expired or tampered token");
    }

    @Test
    @DisplayName("Should extract username correctly from token subject")
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        String token = jwtService.generateAccessToken(mockUser);
        String username = jwtService.getUsernameFromToken(token);

        assertThat(username).isEqualTo("jess@perrosygatos.cl");
    }

    @Test
    @DisplayName("Should extract user ID correctly from custom claim")
    void getUserIdFromToken_shouldReturnCorrectId() {
        String token = jwtService.generateAccessToken(mockUser);
        Long id = jwtService.getUserIdFromToken(token);

        assertThat(id).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should extract unique JTI identifier correctly from token")
    void getJtiFromToken_shouldReturnValidUuidString() {
        String token = jwtService.generateAccessToken(mockUser);
        String jti = jwtService.getJtiFromToken(token);

        assertThat(jti).isNotNull();
        assertThat(jti).matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    @Test
    @DisplayName("Token with altered signature should throw InvalidCredentialsException")
    void getAllClaims_withAlteredSignature_shouldThrowInvalidCredentialsException() {
        String validToken = jwtService.generateAccessToken(mockUser);
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        assertThatThrownBy(() -> jwtService.getAllClaims(tamperedToken))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid, expired or tampered token");
    }

    @Test
    @DisplayName("Invalid JWT string structure should throw InvalidCredentialsException")
    void getAllClaims_withInvalidStructure_shouldThrowInvalidCredentialsException() {
        assertThatThrownBy(() -> jwtService.getAllClaims("this.is.not.a.valid.jwt"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid, expired or tampered token");
    }
}