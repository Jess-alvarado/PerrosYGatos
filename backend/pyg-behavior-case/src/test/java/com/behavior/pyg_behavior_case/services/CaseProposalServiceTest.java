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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseProposalServiceTest {

    @Mock
    private CaseProposalRepository proposalRepository;

    @Mock
    private BehaviorCaseService caseService;

    @InjectMocks
    private CaseProposalService proposalService;

    private static final Long OWNER_ID = 1L;
    private static final Long PROFESSIONAL_ID = 2L;
    private static final Long CASE_ID = 10L;
    private static final Long PROPOSAL_ID = 100L;

    private BehaviorCase openCase;
    private BehaviorCase inProgressCase;
    private CaseProposal pendingProposal;
    private CaseProposalRequest testRequest;

    @BeforeEach
    void setUp() {
        openCase = BehaviorCase.builder()
                .id(CASE_ID)
                .ownerId(OWNER_ID)
                .status(CaseStatus.OPEN)
                .pets(List.of())
                .build();

        inProgressCase = BehaviorCase.builder()
                .id(CASE_ID)
                .ownerId(OWNER_ID)
                .status(CaseStatus.IN_PROGRESS)
                .pets(List.of())
                .build();

        pendingProposal = CaseProposal.builder()
                .id(PROPOSAL_ID)
                .behaviorCase(openCase)
                .professionalId(PROFESSIONAL_ID)
                .approach("Contracondicionamiento clásico")
                .estimatedPrice(45000)
                .estimatedSessions(4)
                .status(ProposalStatus.PENDING)
                .build();

        testRequest = new CaseProposalRequest(
                "Contracondicionamiento clásico",
                45000,
                4
        );
    }

    // ── PROPOSE ──────────────────────────────────────────────

    @Test
    @DisplayName("Professional submits proposal successfully")
    void propose_withValidRequest_shouldReturnResponse() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(openCase);
        when(proposalRepository.existsByBehaviorCaseIdAndProfessionalId(
                CASE_ID, PROFESSIONAL_ID)).thenReturn(false);
        when(proposalRepository.save(any(CaseProposal.class)))
                .thenAnswer(inv -> {
                    CaseProposal p = inv.getArgument(0);
                    p.setId(PROPOSAL_ID);
                    return p;
                });

        CaseProposalResponse response = proposalService.propose(
                CASE_ID, PROFESSIONAL_ID, testRequest);

        assertThat(response.id()).isEqualTo(PROPOSAL_ID);
        assertThat(response.status()).isEqualTo(ProposalStatus.PENDING);
        assertThat(response.estimatedPrice()).isEqualTo(45000);
        verify(proposalRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Throws 409 when professional proposes twice on same case")
    void propose_whenAlreadyProposed_shouldThrow409() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(openCase);
        when(proposalRepository.existsByBehaviorCaseIdAndProfessionalId(
                CASE_ID, PROFESSIONAL_ID)).thenReturn(true);

        assertThatThrownBy(() -> proposalService.propose(
                CASE_ID, PROFESSIONAL_ID, testRequest))
                .isInstanceOf(AlreadyExistsException.class);

        verify(proposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws 409 when case is not OPEN")
    void propose_whenCaseNotOpen_shouldThrow409() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(inProgressCase);

        assertThatThrownBy(() -> proposalService.propose(
                CASE_ID, PROFESSIONAL_ID, testRequest))
                .isInstanceOf(InvalidStateException.class);

        verify(proposalRepository, never()).save(any());
    }

    // ── GET PROPOSALS ─────────────────────────────────────────

    @Test
    @DisplayName("Owner can view proposals for their case")
    void getProposals_byOwner_shouldReturnList() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(openCase);
        when(proposalRepository.findByBehaviorCaseId(CASE_ID))
                .thenReturn(List.of(pendingProposal));

        List<CaseProposalResponse> responses = proposalService.getProposals(
                CASE_ID, OWNER_ID);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).status()).isEqualTo(ProposalStatus.PENDING);
    }

    @Test
    @DisplayName("Throws 403 when non-owner tries to view proposals")
    void getProposals_byNonOwner_shouldThrow403() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(openCase);

        assertThatThrownBy(() -> proposalService.getProposals(CASE_ID, 999L))
                .isInstanceOf(ForbiddenRoleException.class);

        verify(proposalRepository, never()).findByBehaviorCaseId(any());
    }

    // ── ACCEPT PROPOSAL ───────────────────────────────────────

    @Test
    @DisplayName("Owner accepts proposal successfully — case moves to IN_PROGRESS")
    void acceptProposal_withValidRequest_shouldSucceed() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(openCase);
        when(proposalRepository.findById(PROPOSAL_ID))
                .thenReturn(Optional.of(pendingProposal));
        when(proposalRepository.save(any())).thenReturn(pendingProposal);

        CaseProposalResponse response = proposalService.acceptProposal(
                CASE_ID, PROPOSAL_ID, OWNER_ID);

        assertThat(response.status()).isEqualTo(ProposalStatus.ACCEPTED);
        verify(proposalRepository, times(1)).updateAllExcept(
                CASE_ID, PROPOSAL_ID,
                ProposalStatus.PENDING, ProposalStatus.REJECTED);
    }

    @Test
    @DisplayName("Throws 403 when non-owner tries to accept proposal")
    void acceptProposal_byNonOwner_shouldThrow403() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(openCase);

        assertThatThrownBy(() -> proposalService.acceptProposal(
                CASE_ID, PROPOSAL_ID, 999L))
                .isInstanceOf(ForbiddenRoleException.class);

        verify(proposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws 409 when trying to accept proposal on non-OPEN case")
    void acceptProposal_whenCaseNotOpen_shouldThrow409() {
        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(inProgressCase);

        assertThatThrownBy(() -> proposalService.acceptProposal(
                CASE_ID, PROPOSAL_ID, OWNER_ID))
                .isInstanceOf(InvalidStateException.class);

        verify(proposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws 409 when proposal is not PENDING")
    void acceptProposal_whenProposalNotPending_shouldThrow409() {
        CaseProposal withdrawnProposal = CaseProposal.builder()
                .id(PROPOSAL_ID)
                .behaviorCase(openCase)
                .professionalId(PROFESSIONAL_ID)
                .status(ProposalStatus.WITHDRAWN)
                .build();

        when(caseService.findCaseOrThrow(CASE_ID)).thenReturn(openCase);
        when(proposalRepository.findById(PROPOSAL_ID))
                .thenReturn(Optional.of(withdrawnProposal));

        assertThatThrownBy(() -> proposalService.acceptProposal(
                CASE_ID, PROPOSAL_ID, OWNER_ID))
                .isInstanceOf(InvalidStateException.class);

        verify(proposalRepository, never()).updateAllExcept(
                any(), any(), any(), any());
    }

    // ── WITHDRAW PROPOSAL ─────────────────────────────────────

    @Test
    @DisplayName("Professional withdraws their pending proposal successfully")
    void withdrawProposal_withValidRequest_shouldSucceed() {
        when(proposalRepository.findById(PROPOSAL_ID))
                .thenReturn(Optional.of(pendingProposal));
        when(proposalRepository.save(any())).thenReturn(pendingProposal);

        CaseProposalResponse response = proposalService.withdrawProposal(
                CASE_ID, PROPOSAL_ID, PROFESSIONAL_ID);

        assertThat(response.status()).isEqualTo(ProposalStatus.WITHDRAWN);
        verify(proposalRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Throws 403 when professional tries to withdraw another's proposal")
    void withdrawProposal_byWrongProfessional_shouldThrow403() {
        when(proposalRepository.findById(PROPOSAL_ID))
                .thenReturn(Optional.of(pendingProposal));

        assertThatThrownBy(() -> proposalService.withdrawProposal(
                CASE_ID, PROPOSAL_ID, 999L))
                .isInstanceOf(ForbiddenRoleException.class);

        verify(proposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws 409 when trying to withdraw non-PENDING proposal")
    void withdrawProposal_whenNotPending_shouldThrow409() {
        CaseProposal acceptedProposal = CaseProposal.builder()
                .id(PROPOSAL_ID)
                .behaviorCase(openCase)
                .professionalId(PROFESSIONAL_ID)
                .status(ProposalStatus.ACCEPTED)
                .build();

        when(proposalRepository.findById(PROPOSAL_ID))
                .thenReturn(Optional.of(acceptedProposal));

        assertThatThrownBy(() -> proposalService.withdrawProposal(
                CASE_ID, PROPOSAL_ID, PROFESSIONAL_ID))
                .isInstanceOf(InvalidStateException.class);

        verify(proposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws 404 when proposal not found")
    void withdrawProposal_withInvalidId_shouldThrow404() {
        when(proposalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proposalService.withdrawProposal(
                CASE_ID, 999L, PROFESSIONAL_ID))
                .isInstanceOf(NotFoundException.class);
    }
}