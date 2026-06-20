package com.auth.pyg_auth.services;

import com.auth.pyg_auth.dto.requests.LoginRequest;
import com.auth.pyg_auth.dto.requests.RefreshTokenRequest;
import com.auth.pyg_auth.dto.requests.UserRegisterRequest;
import com.auth.pyg_auth.dto.responses.AuthResponse;
import com.auth.pyg_auth.exceptions.AlreadyExistsException;
import com.auth.pyg_auth.exceptions.InvalidCredentialsException;
import com.auth.pyg_auth.exceptions.NotFoundException;
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
import java.util.Date;
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

    private User mockUser;
    private Role mockRole;
    private RefreshToken mockRefreshToken;

    @BeforeEach
    void setUp() {
        mockRole = Role.builder()
                .id(1L)
                .name("ROLE_OWNER")
                .build();

        mockUser = User.builder()
                .id(1L)
                .username("jess@perrosygatos.cl")
                .password("encoded_password")
                .firstname("Jess")
                .lastname("Alvarado")
                .role(mockRole)
                .build();

        mockRefreshToken = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-uuid-string")
                .user(mockUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }

    @Test
    @DisplayName("Login with valid credentials should return AuthResponse")
    void login_withValidCredentials_shouldReturnAuthResponse() {
        LoginRequest request = LoginRequest.builder()
                .username("jess@perrosygatos.cl")
                .password("password123")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("jess@perrosygatos.cl"))
                .thenReturn(Optional.of(mockUser));
        when(jwtService.generateAccessToken(mockUser))
                .thenReturn("access-token-string");
        when(refreshTokenService.createRefreshToken(mockUser))
                .thenReturn(mockRefreshToken);

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token-string");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-uuid-string");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Login with non existent user should throw InvalidCredentialsException")
    void login_withNonExistentUser_shouldThrowInvalidCredentialsException() {
        LoginRequest request = LoginRequest.builder()
                .username("missing@perrosygatos.cl")
                .password("anyPassword")
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("missing@perrosygatos.cl"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    @DisplayName("Login with invalid credentials should throw InvalidCredentialsException")
    void login_withInvalidCredentials_shouldThrowInvalidCredentialsException() {
        LoginRequest request = LoginRequest.builder()
                .username("jess@perrosygatos.cl")
                .password("wrong_password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));


        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid username or password");

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Register with valid data should save user and return tokens")
    void register_withValidData_shouldSaveUserAndReturnAuthResponse() {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("newuser@perrosygatos.cl")
                .password("password123")
                .firstname("Alex")
                .lastname("Smith")
                .rolename("ROLE_OWNER")
                .build();

        when(userRepository.existsByUsername("newuser@perrosygatos.cl")).thenReturn(false);
        when(roleRepository.findByName("ROLE_OWNER")).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode("password123")).thenReturn("bcrypt_encoded_string");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("new-access-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(mockRefreshToken);

        AuthResponse response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-uuid-string");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register with already existing username should throw AlreadyExistsException")
    void register_withExistingUsername_shouldThrowAlreadyExistsException() {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("jess@perrosygatos.cl")
                .password("password123")
                .rolename("ROLE_OWNER")
                .build();

        when(userRepository.existsByUsername("jess@perrosygatos.cl")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Register with non existent role should throw NotFoundException")
    void register_withNonExistentRole_shouldThrowNotFoundException() {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("newuser@perrosygatos.cl")
                .password("password123")
                .rolename("ROLE_INVALID")
                .build();

        when(userRepository.existsByUsername("newuser@perrosygatos.cl")).thenReturn(false);
        when(roleRepository.findByName("ROLE_INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Role not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Refresh with valid token should revoke old and create new tokens")
    void refresh_withValidToken_shouldRotateTokens() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refresh-token-uuid-string")
                .build();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .id(2L)
                .token("brand-new-refresh-token")
                .user(mockUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        when(refreshTokenService.validateRefreshToken("refresh-token-uuid-string"))
                .thenReturn(mockRefreshToken);
        when(jwtService.generateAccessToken(mockUser))
                .thenReturn("refreshed-access-token");
        when(refreshTokenService.createRefreshToken(mockUser))
                .thenReturn(newRefreshToken);

        AuthResponse response = authService.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo("refreshed-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("brand-new-refresh-token");

        verify(refreshTokenService, times(1)).revokeToken("refresh-token-uuid-string");
    }

    @Test
    @DisplayName("Refresh with invalid or expired token should throw InvalidCredentialsException")
    void refresh_withInvalidToken_shouldThrowInvalidCredentialsException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("broken-token")
                .build();

        when(refreshTokenService.validateRefreshToken("broken-token"))
                .thenThrow(new RuntimeException("Token expired or invalid"));

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    @DisplayName("Logout should blacklist JWT in Redis and revoke all refresh tokens")
    void logout_shouldBlacklistJwtAndRevokeRefreshTokens() {
        String accessToken = "fake-valid-access-token";
        String mockedJti = "mocked-jti-uuid";
        long longTimeInFuture = System.currentTimeMillis() + 3600000L;
        Date expirationDate = new Date(longTimeInFuture);

        when(jwtService.getUserIdFromToken(accessToken)).thenReturn(1L);
        when(jwtService.getJtiFromToken(accessToken)).thenReturn(mockedJti);
        when(jwtService.getExpirationDateFromToken(accessToken)).thenReturn(expirationDate);

        authService.logout(accessToken);

        verify(accessTokenBlacklistService, times(1))
                .blacklist(eq(mockedJti), anyLong());

        verify(refreshTokenService, times(1))
                .revokeAllUserTokens(1L);
    }
}