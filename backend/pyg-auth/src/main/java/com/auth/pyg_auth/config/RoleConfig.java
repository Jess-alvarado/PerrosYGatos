package com.auth.pyg_auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.auth.pyg_auth.repositories.RoleRepository;
import com.auth.pyg_auth.models.Role;

@Configuration
public class RoleConfig {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // Create roles if they do not exist
            if (roleRepository.findByName("ROLE_OWNER").isEmpty()) {
                roleRepository.save(Role.builder()
                    .name("ROLE_OWNER")
                    .build());
                System.out.println("ROLE_OWNER created");
            }

            if (roleRepository.findByName("ROLE_PROFESSIONAL").isEmpty()) {
                roleRepository.save(Role.builder()
                    .name("ROLE_PROFESSIONAL")
                    .build());
                System.out.println("ROLE_PROFESSIONAL created");
            }
        };
    }
}