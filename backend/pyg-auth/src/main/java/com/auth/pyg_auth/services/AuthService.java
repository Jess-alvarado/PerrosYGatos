package com.auth.pyg_auth.services;
import org.springframework.stereotype.Service;

import com.auth.pyg_auth.models.AuthResponse;
import com.auth.pyg_auth.models.LoginRequest;
import com.auth.pyg_auth.models.Professional;
import com.auth.pyg_auth.models.UserRegisterRequest;
import com.auth.pyg_auth.models.Role;

import lombok.RequiredArgsConstructor;
import com.auth.pyg_auth.models.User;
import com.auth.pyg_auth.repositories.UserRepository;
import com.auth.pyg_auth.repositories.RoleRepository;
import com.auth.pyg_auth.repositories.ProfessionalRepository;
import com.auth.pyg_auth.models.ProfessionalRegisterRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfessionalRepository professionalRepository;

    public AuthResponse login(LoginRequest request) {
        return null;
    }

    public AuthResponse ownerRegister(UserRegisterRequest request) {
        Role role = roleRepository.findByName(request.getRolename())
        .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getUsername())
                .password(request.getPassword())
                .country(request.getCountry())
                .role(role)
                .pets(request.getPets())
                .build();
        userRepository.save(user);

        return AuthResponse.builder()
                .token(null)
                .build();
    }

        public AuthResponse professionalRegister(ProfessionalRegisterRequest request) {
        Role role = roleRepository.findByName(request.getRoleName())
        .orElseThrow(() -> new RuntimeException("Role not found"));

        Professional professional = Professional.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getUserName())
                .password(request.getPassword())
                .country(request.getCountry())
                .role(role)
                .build();
        professionalRepository.save(professional);

        return AuthResponse.builder()
                .token(null)
                .build();
    }
}