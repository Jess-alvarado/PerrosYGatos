package com.behavior.pyg_behavior_case.repositories;


import com.behavior.pyg_behavior_case.models.BehaviorCase;
import com.behavior.pyg_behavior_case.models.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BehaviorCaseRepository extends JpaRepository<BehaviorCase, Long> {

    // Todos los casos de un dueño
    List<BehaviorCase> findByOwnerId(Long ownerId);

    // Casos por estado (feed de profesionales — solo OPEN)
    List<BehaviorCase> findByStatus(CaseStatus status);

    // Casos de un dueño filtrados por estado
    List<BehaviorCase> findByOwnerIdAndStatus(Long ownerId, CaseStatus status);
}