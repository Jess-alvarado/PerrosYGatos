package com.behavior.pyg_behavior_case.repositories;

import com.behavior.pyg_behavior_case.models.CasePet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasePetRepository extends JpaRepository<CasePet, Long> {

    // Todas las mascotas de un caso
    List<CasePet> findByBehaviorCaseId(Long caseId);
}