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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenBlacklistService accessTokenBlacklistService;

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public AuthResponse register(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistsException("Username already exists");
        }

        Role role = roleRepository.findByName(request.getRolename())
                .orElseThrow(() -> new NotFoundException("Role not found: " + request.getRolename()));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        try {
            RefreshToken currentRefreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
            User user = currentRefreshToken.getUser();

            refreshTokenService.revokeToken(currentRefreshToken.getToken());

            String newAccessToken = jwtService.generateAccessToken(user);
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .tokenType("Bearer")
                    .build();
        } catch (Exception ex) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }
    }

    @Transactional
    public void logout(String accessToken) {
        Long userId = jwtService.getUserIdFromToken(accessToken);
        String jti = jwtService.getJtiFromToken(accessToken);
        Date expiration = jwtService.getExpirationDateFromToken(accessToken);
        long ttlMillis = expiration.getTime() - System.currentTimeMillis();

        accessTokenBlacklistService.blacklist(jti, ttlMillis);
        refreshTokenService.revokeAllUserTokens(userId);
    }
}