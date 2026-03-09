package com.professional.pyg_professional.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.professional.pyg_professional.models.ProfessionalProfile;

@Repository
public interface ProfessionalRepository extends JpaRepository<ProfessionalProfile, Long> {
    Optional<ProfessionalProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}