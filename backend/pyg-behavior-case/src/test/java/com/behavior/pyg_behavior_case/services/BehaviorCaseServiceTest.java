package com.behavior.pyg_behavior_case.services;

import com.behavior.pyg_behavior_case.dto.requests.BehaviorCaseRequest;
import com.behavior.pyg_behavior_case.dto.requests.CasePetSnapshot;
import com.behavior.pyg_behavior_case.dto.responses.BehaviorCaseResponse;
import com.behavior.pyg_behavior_case.exceptions.ForbiddenRoleException;
import com.behavior.pyg_behavior_case.exceptions.InvalidStateException;
import com.behavior.pyg_behavior_case.exceptions.NotFoundException;
import com.behavior.pyg_behavior_case.models.BehaviorCase;
import com.behavior.pyg_behavior_case.models.CaseStatus;
import com.behavior.pyg_behavior_case.models.PetType;
import com.behavior.pyg_behavior_case.repositories.BehaviorCaseRepository;
import com.behavior.pyg_behavior_case.repositories.CasePetRepository;
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
class BehaviorCaseServiceTest {

    @Mock
    private BehaviorCaseRepository caseRepository;

    @Mock
    private CasePetRepository casePetRepository;

    @InjectMocks
    private BehaviorCaseService caseService;

    private static final Long OWNER_ID = 1L;
    private static final Long CASE_ID = 10L;

    private BehaviorCaseRequest testRequest;
    private BehaviorCase testCase;

    @BeforeEach
    void setUp() {
        testRequest = new BehaviorCaseRequest(
                "Mi perro ataca a mi gato",
                "Descripción general del problema",
                "Descripción detallada",
                false,
                true,
                true,
                false,
                "2 semanas",
                List.of(new CasePetSnapshot(
                        1L, "Max", PetType.DOG,
                        "Labrador", 3, true, "M",
                        "Muy activo"))
        );

        testCase = BehaviorCase.builder()
                .id(CASE_ID)
                .ownerId(OWNER_ID)
                .title("Mi perro ataca a mi gato")
                .description("Descripción general del problema")
                .status(CaseStatus.OPEN)
                .pets(List.of())
                .build();
    }

    @Test
    @DisplayName("Creates case successfully with pet snapshot")
    void createCase_withValidRequest_shouldReturnResponse() {
        when(caseRepository.save(any(BehaviorCase.class)))
                .thenAnswer(inv -> {
                    BehaviorCase c = inv.getArgument(0);
                    c.setId(CASE_ID);
                    return c;
                });
        when(casePetRepository.saveAll(any())).thenReturn(List.of());

        BehaviorCaseResponse response = caseService.createCase(OWNER_ID, testRequest);

        assertThat(response.id()).isEqualTo(CASE_ID);
        assertThat(response.ownerId()).isEqualTo(OWNER_ID);
        assertThat(response.status()).isEqualTo(CaseStatus.OPEN);
        assertThat(response.title()).isEqualTo("Mi perro ataca a mi gato");
        verify(caseRepository, times(1)).save(any(BehaviorCase.class));
        verify(casePetRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Returns only OPEN cases for professional feed")
    void getOpenCases_shouldReturnOnlyOpenCases() {
        when(caseRepository.findByStatus(CaseStatus.OPEN))
                .thenReturn(List.of(testCase));

        List<BehaviorCaseResponse> responses = caseService.getOpenCases();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).status()).isEqualTo(CaseStatus.OPEN);
        verify(caseRepository, times(1)).findByStatus(CaseStatus.OPEN);
    }

    @Test
    @DisplayName("Returns empty list when no open cases exist")
    void getOpenCases_withNoCases_shouldReturnEmptyList() {
        when(caseRepository.findByStatus(CaseStatus.OPEN)).thenReturn(List.of());

        List<BehaviorCaseResponse> responses = caseService.getOpenCases();

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Returns owner's cases")
    void getMyCases_withValidOwner_shouldReturnCases() {
        when(caseRepository.findByOwnerId(OWNER_ID)).thenReturn(List.of(testCase));

        List<BehaviorCaseResponse> responses = caseService.getMyCases(OWNER_ID);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).ownerId()).isEqualTo(OWNER_ID);
    }

    @Test
    @DisplayName("Returns case by ID")
    void getCaseById_withValidId_shouldReturnCase() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(testCase));

        BehaviorCaseResponse response = caseService.getCaseById(CASE_ID);

        assertThat(response.id()).isEqualTo(CASE_ID);
    }

    @Test
    @DisplayName("Throws 404 when case not found")
    void getCaseById_withInvalidId_shouldThrow404() {
        when(caseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caseService.getCaseById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Updates case status from OPEN to CANCELLED successfully")
    void updateStatus_fromOpenToCancelled_shouldSucceed() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(testCase));
        when(caseRepository.save(any())).thenReturn(testCase);

        BehaviorCaseResponse response = caseService.updateStatus(
                CASE_ID, OWNER_ID, CaseStatus.CANCELLED);

        assertThat(response).isNotNull();
        verify(caseRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Throws 409 for invalid status transition OPEN to RESOLVED")
    void updateStatus_invalidTransition_shouldThrow409() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(testCase));

        assertThatThrownBy(() -> caseService.updateStatus(
                CASE_ID, OWNER_ID, CaseStatus.RESOLVED))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    @DisplayName("Throws 403 when non-owner tries to update status")
    void updateStatus_byNonOwner_shouldThrow403() {
        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(testCase));

        assertThatThrownBy(() -> caseService.updateStatus(
                CASE_ID, 999L, CaseStatus.CANCELLED))
                .isInstanceOf(ForbiddenRoleException.class);
    }

    @Test
    @DisplayName("Throws 409 for all invalid transitions from final states")
    void updateStatus_fromFinalState_shouldThrow409() {
        BehaviorCase resolvedCase = BehaviorCase.builder()
                .id(CASE_ID)
                .ownerId(OWNER_ID)
                .status(CaseStatus.RESOLVED)
                .pets(List.of())
                .build();

        when(caseRepository.findById(CASE_ID)).thenReturn(Optional.of(resolvedCase));

        assertThatThrownBy(() -> caseService.updateStatus(
                CASE_ID, OWNER_ID, CaseStatus.OPEN))
                .isInstanceOf(InvalidStateException.class);
    }
}