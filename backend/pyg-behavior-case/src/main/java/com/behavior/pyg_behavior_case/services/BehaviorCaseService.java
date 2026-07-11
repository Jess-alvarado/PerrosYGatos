package com.behavior.pyg_behavior_case.services;

import com.behavior.pyg_behavior_case.dto.requests.BehaviorCaseRequest;
import com.behavior.pyg_behavior_case.dto.responses.BehaviorCaseResponse;
import com.behavior.pyg_behavior_case.dto.responses.CasePetSnapshotResponse;
import com.behavior.pyg_behavior_case.exceptions.*;
import com.behavior.pyg_behavior_case.models.*;
import com.behavior.pyg_behavior_case.repositories.BehaviorCaseRepository;
import com.behavior.pyg_behavior_case.repositories.CasePetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BehaviorCaseService {

    private final BehaviorCaseRepository caseRepository;
    private final CasePetRepository casePetRepository;

    @Transactional
    public BehaviorCaseResponse createCase(Long ownerId, BehaviorCaseRequest request) {

        BehaviorCase behaviorCase = BehaviorCase.builder()
                .ownerId(ownerId)
                .title(request.title())
                .description(request.description())
                .detailedDescription(request.detailedDescription())
                .hasChildren(request.hasChildren())
                .hasOtherPets(request.hasOtherPets())
                .hasAggression(request.hasAggression())
                .isAloneFrequently(request.isAloneFrequently())
                .behaviorDuration(request.behaviorDuration())
                .status(CaseStatus.OPEN)
                .build();

        BehaviorCase saved = caseRepository.save(behaviorCase);

        // Guardamos el snapshot de cada mascota involucrada
        List<CasePet> pets = request.pets().stream()
                .map(petSnapshot -> CasePet.builder()
                        .behaviorCase(saved)
                        .originalPetId(petSnapshot.originalPetId())
                        .name(petSnapshot.name())
                        .type(petSnapshot.type())
                        .breed(petSnapshot.breed())
                        .age(petSnapshot.age())
                        .sterilized(petSnapshot.sterilized())
                        .sex(petSnapshot.sex())
                        .personalityDescription(petSnapshot.personalityDescription())
                        .build())
                .collect(Collectors.toList());

        casePetRepository.saveAll(pets);
        saved.setPets(pets);

        return toResponse(saved);
    }

    // Feed para profesionales — solo casos OPEN
    public List<BehaviorCaseResponse> getOpenCases() {
        return caseRepository.findByStatus(CaseStatus.OPEN)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Mis casos — para el dueño
    public List<BehaviorCaseResponse> getMyCases(Long ownerId) {
        return caseRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Detalle de un caso
    public BehaviorCaseResponse getCaseById(Long caseId) {
        return toResponse(findCaseOrThrow(caseId));
    }

    // Cambio de estado — con validación de transiciones
    @Transactional
    public BehaviorCaseResponse updateStatus(Long caseId, Long ownerId,
                                             CaseStatus newStatus) {

        BehaviorCase behaviorCase = findCaseOrThrow(caseId);

        // Solo el dueño puede cambiar el estado
        if (!behaviorCase.getOwnerId().equals(ownerId)) {
            throw new ForbiddenRoleException("Only the owner can change the case status");
        }

        validateStatusTransition(behaviorCase.getStatus(), newStatus);

        behaviorCase.setStatus(newStatus);
        return toResponse(caseRepository.save(behaviorCase));
    }

    // Validación de transiciones válidas
    private void validateStatusTransition(CaseStatus current, CaseStatus next) {
        boolean valid = switch (current) {
            case OPEN -> next == CaseStatus.CANCELLED;
            case IN_PROGRESS -> next == CaseStatus.RESOLVED
                    || next == CaseStatus.ABANDONED;
            case RESOLVED, CANCELLED, ABANDONED -> false; // estados finales
        };

        if (!valid) {
            throw new InvalidStateException(
                    "Cannot transition from " + current + " to " + next);
        }
    }

    BehaviorCase findCaseOrThrow(Long caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found"));
    }

    private BehaviorCaseResponse toResponse(BehaviorCase c) {
        List<CasePetSnapshotResponse> pets = c.getPets() == null
                ? List.of()
                : c.getPets().stream()
                .map(p -> new CasePetSnapshotResponse(
                        p.getId(),
                        p.getOriginalPetId(),
                        p.getName(),
                        p.getType(),
                        p.getBreed(),
                        p.getAge(),
                        p.getSterilized(),
                        p.getSex(),
                        p.getPersonalityDescription()))
                .collect(Collectors.toList());

        return new BehaviorCaseResponse(
                c.getId(),
                c.getOwnerId(),
                c.getTitle(),
                c.getDescription(),
                c.getDetailedDescription(),
                c.getHasChildren(),
                c.getHasOtherPets(),
                c.getHasAggression(),
                c.getIsAloneFrequently(),
                c.getBehaviorDuration(),
                c.getStatus(),
                pets,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}