package com.auth.pyg_auth.services;

import org.springframework.stereotype.Service;

import com.auth.pyg_auth.models.AuthResponse;
import com.auth.pyg_auth.models.LoginRequest;
import com.auth.pyg_auth.models.UserRegisterRequest;
import com.auth.pyg_auth.models.Role;
import com.auth.pyg_auth.models.Token;
// import com.auth.pyg_auth.models.TokenResponse;

import lombok.RequiredArgsConstructor;
import com.auth.pyg_auth.models.User;
import com.auth.pyg_auth.repositories.UserRepository;

import jakarta.transaction.Transactional;

import com.auth.pyg_auth.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final JwtService jwtService;
        private final TokenBlacklistService tokenBlacklistService;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final Token token;

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                request.getUsername(), request.getPassword()));
                UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
                String token = jwtService.getToken(user);
                return AuthResponse.builder()
                                .token(token)
                                .build();
        }

        @Transactional
        public AuthResponse register(UserRegisterRequest request) {
                Role role = roleRepository.findByName(request.getRolename())
                                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRolename()));

                User user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .role(role)
                                .build();
                userRepository.save(user);
                System.out.println("user registered: " + user.getUsername());

                return AuthResponse.builder()
                                .token(jwtService.getToken(user))
                                .build();

        }

        public void logout(String token) {
                if (token == null || token.isBlank()) {
                        return;
                }
                tokenBlacklistService.blacklist(token, jwtService.getExpirationDate(token));
        }

/*
        public TokenResponse refreshToken(String authHeader) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new RuntimeException("Authorization header missing or invalid");
                }
                String refreshToken = authHeader.substring(7);
                if (tokenBlacklistService.isBlacklisted(refreshToken)) {
                        throw new RuntimeException("Token is blacklisted");
                }
                String username = jwtService.extractUsername(refreshToken);
                UserDetails user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                if (!jwtService.isTokenValid(refreshToken, user)) {
                        throw new RuntimeException("Invalid token");
                }
                String newToken = jwtService.getToken(user);
                return TokenResponse.builder()
                                .token(newToken)
                                .build();
        }
*/
}
