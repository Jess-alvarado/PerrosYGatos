package com.auth.pyg_auth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "blacklisted_access_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 2000)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}