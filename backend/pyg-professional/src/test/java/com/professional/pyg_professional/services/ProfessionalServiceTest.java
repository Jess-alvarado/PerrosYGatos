package com.professional.pyg_professional.services;

import com.professional.pyg_professional.clients.AuthServiceClient;
import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.dto.responses.TokenValidationResponse;
import com.professional.pyg_professional.models.ProfessionalProfile;
import com.professional.pyg_professional.repositories.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private ProfessionalService professionalService;

    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.test.signature";
    private static final Long USER_ID = 1L;
    private static final Long PROFILE_ID = 10L;

    private TokenValidationResponse validProfessionalToken;
    private TokenValidationResponse validOwnerToken;
    private TokenValidationResponse invalidToken;
    private ProfessionalProfile testProfile;
    private ProfessionalRequest testRequest;

    @BeforeEach
    void setUp() {
        validProfessionalToken = TokenValidationResponse.builder()
                .userId(USER_ID)
                .username("jess.alvarado")
                .role("ROLE_PROFESSIONAL")
                .valid(true)
                .expiresAt(System.currentTimeMillis() + 3600000L)
                .build();

        validOwnerToken = TokenValidationResponse.builder()
                .userId(USER_ID)
                .username("ana.gonzalez")
                .role("ROLE_OWNER")
                .valid(true)
                .expiresAt(System.currentTimeMillis() + 3600000L)
                .build();

        invalidToken = TokenValidationResponse.builder()
                .userId(null)
                .username(null)
                .role(null)
                .valid(false)
                .expiresAt(null)
                .build();

        testProfile = ProfessionalProfile.builder()
                .id(PROFILE_ID)
                .userId(USER_ID)
                .phone("+56912345678")
                .address("Linares, Chile")
                .birthDate(LocalDate.of(1998, 5, 10))
                .profession("Feline ethologist")
                .bio("Specialist in feline behavior")
                .experienceYears(4)
                .petTypes("CAT")
                .rating(0.0)
                .reviewCount(0)
                .status("ACTIVE")
                .build();

        testRequest = new ProfessionalRequest(
                "+56912345678",
                "Linares, Chile",
                LocalDate.of(1998, 5, 10),
                "Feline ethologist",
                "Specialist in feline behavior",
                4,
                "CAT",
                null,
                null,
                null,
                "Mon-Fri 10:00-18:00"
        );
    }

    @Test
    @DisplayName("Creates profile successfully for a valid professional token")
    void createProfile_withValidProfessionalToken_shouldReturnResponse() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validProfessionalToken);
        when(professionalRepository.existsByUserId(USER_ID)).thenReturn(false);
        when(professionalRepository.save(any(ProfessionalProfile.class)))
                .thenAnswer(invocation -> {
                    ProfessionalProfile p = invocation.getArgument(0);
                    p.setId(PROFILE_ID);
                    return p;
                });

        ProfessionalResponse response = professionalService.createProfile(BEARER_TOKEN, testRequest);

        assertThat(response.id()).isEqualTo(PROFILE_ID);
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.profession()).isEqualTo("Feline ethologist");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.rating()).isEqualTo(0.0);
        assertThat(response.reviewCount()).isEqualTo(0);
        verify(professionalRepository, times(1)).save(any(ProfessionalProfile.class));
    }

    @Test
    @DisplayName("Throws 409 CONFLICT when profile already exists")
    void createProfile_withExistingProfile_shouldThrow409Conflict() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validProfessionalToken);
        when(professionalRepository.existsByUserId(USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> professionalService.createProfile(BEARER_TOKEN, testRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(CONFLICT);
                    assertThat(rse.getReason()).contains("already exists");
                });

        verify(professionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws 403 FORBIDDEN when user role is OWNER, not PROFESSIONAL")
    void createProfile_withOwnerRole_shouldThrow403Forbidden() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validOwnerToken);

        assertThatThrownBy(() -> professionalService.createProfile(BEARER_TOKEN, testRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(FORBIDDEN);
                    assertThat(rse.getReason()).contains("PROFESSIONAL role");
                });

        verify(professionalRepository, never()).existsByUserId(any());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws 401 UNAUTHORIZED when token is invalid")
    void createProfile_withInvalidToken_shouldThrow401Unauthorized() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(invalidToken);

        assertThatThrownBy(() -> professionalService.createProfile(BEARER_TOKEN, testRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(UNAUTHORIZED);
                });

        verify(professionalRepository, never()).existsByUserId(any());
    }

    @Test
    @DisplayName("Auth service failure prevents any repository access in createProfile")
    void createProfile_whenAuthServiceFails_shouldThrowWithoutTouchingRepository() {
        when(authServiceClient.validateToken(BEARER_TOKEN))
                .thenThrow(new RuntimeException("Auth service unavailable"));

        assertThatThrownBy(() -> professionalService.createProfile(BEARER_TOKEN, testRequest))
                .isInstanceOf(RuntimeException.class);

        verify(professionalRepository, never()).existsByUserId(any());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Returns profile for authenticated professional")
    void getMyProfile_withValidToken_shouldReturnProfile() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validProfessionalToken);
        when(professionalRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testProfile));

        ProfessionalResponse response = professionalService.getMyProfile(BEARER_TOKEN);

        assertThat(response.id()).isEqualTo(PROFILE_ID);
        assertThat(response.profession()).isEqualTo("Feline ethologist");
    }

    @Test
    @DisplayName("Throws 404 NOT FOUND when professional has no profile")
    void getMyProfile_withNoProfile_shouldThrow404NotFound() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validProfessionalToken);
        when(professionalRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.getMyProfile(BEARER_TOKEN))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(NOT_FOUND);
                });
    }

    @Test
    @DisplayName("Throws 403 when owner tries to access professional profile endpoint")
    void getMyProfile_withOwnerRole_shouldThrow403Forbidden() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validOwnerToken);

        assertThatThrownBy(() -> professionalService.getMyProfile(BEARER_TOKEN))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(FORBIDDEN)
                );

        verify(professionalRepository, never()).findByUserId(any());
    }


    @Test
    @DisplayName("Updates only non-null fields in the profile")
    void updateMyProfile_withPartialRequest_shouldUpdateOnlyProvidedFields() {
        ProfessionalUpdateRequest partialUpdate = new ProfessionalUpdateRequest(
                "+56999999999", null, null, null,
                "Updated bio", null, null, null,
                null, null, null
        );

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validProfessionalToken);
        when(professionalRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testProfile));
        when(professionalRepository.save(any(ProfessionalProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionalResponse response = professionalService.updateMyProfile(BEARER_TOKEN, partialUpdate);

        assertThat(response.phone()).isEqualTo("+56999999999");
        assertThat(response.bio()).isEqualTo("Updated bio");
        assertThat(response.profession()).isEqualTo("Feline ethologist");
        assertThat(response.address()).isEqualTo("Linares, Chile");
    }

    @Test
    @DisplayName("Throws 404 when trying to update a non-existent profile")
    void updateMyProfile_withNoProfile_shouldThrow404NotFound() {
        ProfessionalUpdateRequest update = new ProfessionalUpdateRequest(
                "+56999999999", null, null, null,
                null, null, null, null,
                null, null, null
        );

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validProfessionalToken);
        when(professionalRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.updateMyProfile(BEARER_TOKEN, update))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(NOT_FOUND)
                );

        verify(professionalRepository, never()).save(any());
    }


    @Test
    @DisplayName("Returns all professionals for any valid token regardless of role")
    void getAllProfessionals_withAnyValidToken_shouldReturnList() {
        // (5) — este endpoint no valida rol, solo que el token sea válido
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validOwnerToken);
        when(professionalRepository.findAll()).thenReturn(List.of(testProfile));

        List<ProfessionalResponse> responses = professionalService.getAllProfessionals(BEARER_TOKEN);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).profession()).isEqualTo("Feline ethologist");
    }

    @Test
    @DisplayName("Returns empty list when no professionals exist")
    void getAllProfessionals_withNoProfessionals_shouldReturnEmptyList() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validProfessionalToken);
        when(professionalRepository.findAll()).thenReturn(List.of());

        List<ProfessionalResponse> responses = professionalService.getAllProfessionals(BEARER_TOKEN);

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Returns professional by ID for any valid token")
    void getProfessionalById_withValidId_shouldReturnProfile() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validOwnerToken);
        when(professionalRepository.findById(PROFILE_ID)).thenReturn(Optional.of(testProfile));

        ProfessionalResponse response = professionalService.getProfessionalById(BEARER_TOKEN, PROFILE_ID);

        assertThat(response.id()).isEqualTo(PROFILE_ID);
        assertThat(response.userId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("Throws 404 when professional ID does not exist")
    void getProfessionalById_withInvalidId_shouldThrow404NotFound() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validOwnerToken);
        when(professionalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.getProfessionalById(BEARER_TOKEN, 999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(NOT_FOUND)
                );
    }
}