package com.behavior.pyg_behavior_case.services;

import com.behavior.pyg_behavior_case.dto.requests.CaseProposalRequest;
import com.behavior.pyg_behavior_case.dto.responses.CaseProposalResponse;
import com.behavior.pyg_behavior_case.exceptions.AlreadyExistsException;
import com.behavior.pyg_behavior_case.exceptions.ForbiddenRoleException;
import com.behavior.pyg_behavior_case.exceptions.InvalidStateException;
import com.behavior.pyg_behavior_case.exceptions.NotFoundException;
import com.behavior.pyg_behavior_case.models.BehaviorCase;
import com.behavior.pyg_behavior_case.models.CaseProposal;
import com.behavior.pyg_behavior_case.models.CaseStatus;
import com.behavior.pyg_behavior_case.models.ProposalStatus;
import com.behavior.pyg_behavior_case.repositories.CaseProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseProposalService {

    private final CaseProposalRepository proposalRepository;
    private final BehaviorCaseService caseService; // para reusar findCaseOrThrow

    @Transactional
    public CaseProposalResponse propose(Long caseId, Long professionalId,
                                        CaseProposalRequest request) {

        BehaviorCase behaviorCase = caseService.findCaseOrThrow(caseId);

        // Solo se puede proponer en casos OPEN
        if (behaviorCase.getStatus() != CaseStatus.OPEN) {
            throw new InvalidStateException(
                    "Cannot propose — case is " + behaviorCase.getStatus());
        }

        // Un profesional no puede proponer dos veces en el mismo caso
        if (proposalRepository.existsByBehaviorCaseIdAndProfessionalId(
                caseId, professionalId)) {
            throw new AlreadyExistsException(
                    "You already submitted a proposal for this case");
        }

        CaseProposal proposal = CaseProposal.builder()
                .behaviorCase(behaviorCase)
                .professionalId(professionalId)
                .approach(request.approach())
                .estimatedPrice(request.estimatedPrice())
                .estimatedSessions(request.estimatedSessions())
                .status(ProposalStatus.PENDING)
                .build();

        return toResponse(proposalRepository.save(proposal));
    }

    // Ver propuestas de un caso — solo el dueño
    public List<CaseProposalResponse> getProposals(Long caseId, Long ownerId) {

        BehaviorCase behaviorCase = caseService.findCaseOrThrow(caseId);

        if (!behaviorCase.getOwnerId().equals(ownerId)) {
            throw new ForbiddenRoleException(
                    "Only the case owner can view proposals");
        }

        return proposalRepository.findByBehaviorCaseId(caseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CaseProposalResponse acceptProposal(Long caseId, Long proposalId,
                                               Long ownerId) {

        BehaviorCase behaviorCase = caseService.findCaseOrThrow(caseId);

        if (!behaviorCase.getOwnerId().equals(ownerId)) {
            throw new ForbiddenRoleException("Only the case owner can accept proposals");
        }

        if (behaviorCase.getStatus() != CaseStatus.OPEN) {
            throw new InvalidStateException(
                    "Cannot accept proposal — case is " + behaviorCase.getStatus());
        }

        CaseProposal proposal = findProposalOrThrow(proposalId);

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new InvalidStateException(
                    "Cannot accept — proposal is " + proposal.getStatus());
        }

        // Acepta la propuesta elegida
        proposal.setStatus(ProposalStatus.ACCEPTED);
        proposalRepository.save(proposal);

        // ✅ Una sola query UPDATE para todas las demás
        proposalRepository.updateAllExcept(
                caseId,
                proposalId,
                ProposalStatus.PENDING,
                ProposalStatus.REJECTED
        );

        // Avanza el estado del caso — @Version protege contra concurrencia
        behaviorCase.setStatus(CaseStatus.IN_PROGRESS);
        // @Transactional + dirty checking guarda el cambio automáticamente

        return toResponse(proposal);
    }

    // Retirar propuesta — solo el profesional que la envió
    @Transactional
    public CaseProposalResponse withdrawProposal(Long caseId, Long proposalId,
                                                 Long professionalId) {

        CaseProposal proposal = findProposalOrThrow(proposalId);

        if (!proposal.getProfessionalId().equals(professionalId)) {
            throw new ForbiddenRoleException(
                    "Only the professional who submitted can withdraw");
        }

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new InvalidStateException(
                    "Cannot withdraw — proposal is " + proposal.getStatus());
        }

        proposal.setStatus(ProposalStatus.WITHDRAWN);
        return toResponse(proposalRepository.save(proposal));
    }

    private CaseProposal findProposalOrThrow(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Proposal not found"));
    }

    private CaseProposalResponse toResponse(CaseProposal p) {
        return new CaseProposalResponse(
                p.getId(),
                p.getProfessionalId(),
                p.getApproach(),
                p.getEstimatedPrice(),
                p.getEstimatedSessions(),
                p.getStatus(),
                p.getCreatedAt()
        );
    }
}