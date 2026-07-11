package com.behavior.pyg_behavior_case.repositories;

import com.behavior.pyg_behavior_case.models.CaseProposal;
import com.behavior.pyg_behavior_case.models.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CaseProposalRepository extends JpaRepository<CaseProposal, Long> {

    List<CaseProposal> findByBehaviorCaseId(Long caseId);

    List<CaseProposal> findByBehaviorCaseIdAndStatus(Long caseId, ProposalStatus status);

    boolean existsByBehaviorCaseIdAndProfessionalId(Long caseId, Long professionalId);

    Optional<CaseProposal> findByBehaviorCaseIdAndProfessionalId(Long caseId, Long professionalId);

    // Una sola query
    @Modifying
    @Query("UPDATE CaseProposal p SET p.status = :newStatus " +
            "WHERE p.behaviorCase.id = :caseId " +
            "AND p.status = :currentStatus " +
            "AND p.id != :excludeId")
    void updateAllExcept(
            @Param("caseId") Long caseId,
            @Param("excludeId") Long excludeProposalId,
            @Param("currentStatus") ProposalStatus currentStatus,
            @Param("newStatus") ProposalStatus newStatus
    );
}