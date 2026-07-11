package com.behavior.pyg_behavior_case.repositories;

import com.behavior.pyg_behavior_case.models.CaseEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseEvidenceRepository extends JpaRepository<CaseEvidence, Long> {

    // Todas las evidencias de un caso
    List<CaseEvidence> findByBehaviorCaseId(Long caseId);
}