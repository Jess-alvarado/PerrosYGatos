package com.auth.pyg_auth.services;

import org.springframework.stereotype.Service;

import com.auth.pyg_auth.dto.requests.LoginRequest;
import com.auth.pyg_auth.dto.requests.UserRegisterRequest;
import com.auth.pyg_auth.dto.responses.AuthResponse;
import com.auth.pyg_auth.models.Role;

import lombok.RequiredArgsConstructor;
import com.auth.pyg_auth.models.User;
import com.auth.pyg_auth.repositories.UserRepository;

import jakarta.transaction.Transactional;

import com.auth.pyg_auth.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                request.getUsername(), request.getPassword()));

                User user = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new RuntimeException("User not found: "));
                String token = jwtService.getToken(user);
                return AuthResponse.builder()
                                .token(token)
                                .build();
        }

        @Transactional
        public AuthResponse register(UserRegisterRequest request) {
                System.out.println("Starting registration for user: " + request.getUsername());

                // 1. Find role
                System.out.println("Searching for role: " + request.getRolename());
                Role role = roleRepository.findByName(request.getRolename())
                                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRolename()));
                System.out.println("Role found: " + role.getName());

                // 2. Create user
                User user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .role(role)
                                .build();
                
                // 3. Save user
                System.out.println("Saving user to DB...");
                userRepository.save(user);
                System.out.println("User registered successfully: " + user.getUsername());

                // 4. Generate token
                System.out.println("Generating JWT token...");
                String token = jwtService.getToken(user);
                System.out.println("Token generated successfully");

                return AuthResponse.builder()
                                .token(token)
                                .build();

        }
}