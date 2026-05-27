package com.auth.pyg_auth.services;

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

    private User usuarioPrueba;

    private static final String SECRET_TEST = "dGVzdFNlY3JldEtleVBhcmFQcnVlYmFzUHlnQXV0aDEyMzQ1Njc4OTA=";
    private static final Long EXPIRACION_MS = 3600000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_TEST);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", EXPIRACION_MS);

        Role rol = new Role();
        rol.setName("ROLE_OWNER");

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
    @DisplayName("Should generate token with all correct claims")
    void generateAccessToken_debeContenerClaimsCorrectas() {
        String token = jwtService.generateAccessToken(usuarioPrueba);

        Claims claims = jwtService.getAllClaims(token);

        assertThat(claims.getSubject()).isEqualTo("ana@perrosgatos.cl");
        assertThat(claims.get("uid", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_OWNER");
        assertThat(claims.get("firstname", String.class)).isEqualTo("Ana");
        assertThat(claims.get("lastname", String.class)).isEqualTo("González");
    }

    @Test
    @DisplayName("Generated token should have a future expiration date")
    void generateAccessToken_debeExpirarEnElFuturo() {
        String token = jwtService.generateAccessToken(usuarioPrueba);
        Date expiracion = jwtService.getExpirationDateFromToken(token);

        assertThat(expiracion).isAfter(new Date());
    }

    @Test
    @DisplayName("Valid token should be recognized as valid")
    void isTokenValid_conTokenValido_debeRetornarTrue() {
        String token = jwtService.generateAccessToken(usuarioPrueba);

        boolean resultado = jwtService.isTokenValid(token, usuarioPrueba);

        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Token from another user should be invalid for current user")
    void isTokenValid_conTokenDeOtroUsuario_debeRetornarFalse() {
        Role rol = new Role();
        rol.setName("ROLE_PROFESSIONAL");

        User otroUsuario = User.builder()
                .id(2L)
                .username("pedro@perrosgatos.cl")
                .password("otro_password")
                .firstname("Pedro")
                .lastname("Soto")
                .role(rol)
                .build();

        String tokenDePedro = jwtService.generateAccessToken(otroUsuario);

        boolean resultado = jwtService.isTokenValid(tokenDePedro, usuarioPrueba);

        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Expired token should throw ExpiredJwtException on validation")
    void isTokenValid_conTokenExpirado_debeLanzarExpiredJwtException() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1L);
        String tokenExpirado = jwtService.generateAccessToken(usuarioPrueba);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", EXPIRACION_MS);

        assertThatThrownBy(() -> jwtService.isTokenValid(tokenExpirado, usuarioPrueba))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Should extract username correctly from token")
    void getUsernameFromToken_debeRetornarUsernameCorrect() {
        String token = jwtService.generateAccessToken(usuarioPrueba);

        String username = jwtService.getUsernameFromToken(token);

        assertThat(username).isEqualTo("ana@perrosgatos.cl");
    }

    @Test
    @DisplayName("Should extract user ID correctly from token")
    void getUserIdFromToken_debeRetornarIdCorrecto() {
        String token = jwtService.generateAccessToken(usuarioPrueba);

        Long id = jwtService.getUserIdFromToken(token);

        assertThat(id).isEqualTo(1L);
    }

    @Test
    @DisplayName("Token with altered signature should throw exception")
    void getAllClaims_conTokenFalsificado_debeLanzarExcepcion() {
        String tokenValido = jwtService.generateAccessToken(usuarioPrueba);
        String tokenFalsificado = tokenValido.substring(0, tokenValido.length() - 5) + "XXXXX";

        assertThatThrownBy(() -> jwtService.getAllClaims(tokenFalsificado))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Invalid JWT string structure should throw exception")
    void getAllClaims_conStringInvalido_debeLanzarExcepcion() {
        assertThatThrownBy(() -> jwtService.getAllClaims("esto.no.es.un.jwt"))
                .isInstanceOf(Exception.class);
    }
}