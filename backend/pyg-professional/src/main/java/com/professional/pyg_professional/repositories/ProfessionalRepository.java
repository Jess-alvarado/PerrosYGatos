package com.professional.pyg_professional.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

import com.professional.pyg_professional.models.ProfessionalProfile;

@Controller
public interface ProfessionalRepository extends JpaRepository<ProfessionalProfile, Long> {
    Optional<ProfessionalProfile> findByUserId(Long userId);
}
