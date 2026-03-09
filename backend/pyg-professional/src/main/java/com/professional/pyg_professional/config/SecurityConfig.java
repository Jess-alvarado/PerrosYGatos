package com.professional.pyg_professional.config;

import com.professional.pyg_professional.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Solo profesionales pueden crear y gestionar su propio perfil
                        .requestMatchers(HttpMethod.POST, "/professionals").hasRole("PROFESSIONAL")
                        .requestMatchers(HttpMethod.GET, "/professionals/profile").hasRole("PROFESSIONAL")
                        .requestMatchers(HttpMethod.PATCH, "/professionals/profile").hasRole("PROFESSIONAL")

                        // Owners y professionals pueden consultar perfiles de profesionales
                        .requestMatchers(HttpMethod.GET, "/professionals").hasAnyRole("OWNER", "PROFESSIONAL")
                        .requestMatchers(HttpMethod.GET, "/professionals/{id}").hasAnyRole("OWNER", "PROFESSIONAL")
                        .requestMatchers(HttpMethod.GET, "/professionals/*").hasAnyRole("OWNER", "PROFESSIONAL")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}