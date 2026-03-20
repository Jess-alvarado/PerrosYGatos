package com.auth.pyg_auth.repositories;

import com.auth.pyg_auth.models.BlacklistedAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedAccessTokenRepository extends JpaRepository<BlacklistedAccessToken, Long> {
    boolean existsByToken(String token);
}