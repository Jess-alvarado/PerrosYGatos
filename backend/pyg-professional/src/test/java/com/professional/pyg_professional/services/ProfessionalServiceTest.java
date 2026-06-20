package com.professional.pyg_professional.services;

import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.exceptions.AlreadyExistsException;
import com.professional.pyg_professional.exceptions.NotFoundException;
import com.professional.pyg_professional.exceptions.ValidationException;
import com.professional.pyg_professional.models.ProfessionalProfile;
import com.professional.pyg_professional.repositories.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private ProfessionalService professionalService;

    private static final Long TEST_USER_ID = 123L;
    private static final Long PROFILE_ID = 10L;

    private ProfessionalProfile testProfile;
    private ProfessionalRequest testRequest;

    @BeforeEach
    void setUp() {
        testProfile = ProfessionalProfile.builder()
                .id(PROFILE_ID)
                .userId(TEST_USER_ID)
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
    @DisplayName("Creates profile successfully for a valid userId")
    void createProfile_withValidData_shouldReturnResponse() {
        Mockito.when(professionalRepository.existsByUserId(TEST_USER_ID)).thenReturn(false);
        Mockito.when(professionalRepository.save(any(ProfessionalProfile.class)))
                .thenAnswer(invocation -> {
                    ProfessionalProfile p = invocation.getArgument(0);
                    p.setId(PROFILE_ID);
                    return p;
                });

        ProfessionalResponse response = professionalService.createProfile(TEST_USER_ID, testRequest);

        assertThat(response.id()).isEqualTo(PROFILE_ID);
        assertThat(response.userId()).isEqualTo(TEST_USER_ID);
        assertThat(response.profession()).isEqualTo("Feline ethologist");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.rating()).isEqualTo(0.0);
        assertThat(response.reviewCount()).isEqualTo(0);
        Mockito.verify(professionalRepository, Mockito.times(1)).save(any(ProfessionalProfile.class));
    }

    @Test
    @DisplayName("Throws AlreadyExistsException when professional profile already exists")
    void createProfile_withExistingProfile_shouldThrowAlreadyExistsException() {
        Mockito.when(professionalRepository.existsByUserId(TEST_USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> professionalService.createProfile(TEST_USER_ID, testRequest))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Professional profile already exists");

        Mockito.verify(professionalRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Returns profile for an existing user ID")
    void getMyProfile_withExistingProfile_shouldReturnProfile() {
        Mockito.when(professionalRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

        ProfessionalResponse response = professionalService.getMyProfile(TEST_USER_ID);

        assertThat(response.id()).isEqualTo(PROFILE_ID);
        assertThat(response.profession()).isEqualTo("Feline ethologist");
    }

    @Test
    @DisplayName("Throws NotFoundException when getting a profile that does not exist")
    void getMyProfile_withNoProfile_shouldThrowNotFoundException() {
        Mockito.when(professionalRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.getMyProfile(TEST_USER_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Professional profile not found");
    }

    @Test
    @DisplayName("Updates only non-null fields in the professional profile")
    void updateMyProfile_withPartialRequest_shouldUpdateOnlyProvidedFields() {
        ProfessionalUpdateRequest partialUpdate = new ProfessionalUpdateRequest(
                "+56999999999", null, null, null,
                "Updated bio", null, null, null,
                null, null, null
        );

        Mockito.when(professionalRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));
        Mockito.when(professionalRepository.save(any(ProfessionalProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionalResponse response = professionalService.updateMyProfile(TEST_USER_ID, partialUpdate);

        assertThat(response.phone()).isEqualTo("+56999999999");
        assertThat(response.bio()).isEqualTo("Updated bio");
        assertThat(response.profession()).isEqualTo("Feline ethologist");
        assertThat(response.address()).isEqualTo("Linares, Chile");
    }

    @Test
    @DisplayName("Throws NotFoundException when trying to update a non-existent profile")
    void updateMyProfile_withNoProfile_shouldThrowNotFoundException() {
        ProfessionalUpdateRequest update = new ProfessionalUpdateRequest(
                "+56999999999", null, null, null,
                null, null, null, null,
                null, null, null
        );

        Mockito.when(professionalRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.updateMyProfile(TEST_USER_ID, update))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Professional profile not found");

        Mockito.verify(professionalRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Returns all professional profiles in the system")
    void getAllProfessionals_shouldReturnList() {
        Mockito.when(professionalRepository.findAll()).thenReturn(List.of(testProfile));

        List<ProfessionalResponse> responses = professionalService.getAllProfessionals();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).profession()).isEqualTo("Feline ethologist");
    }

    @Test
    @DisplayName("Returns empty list when no professionals exist in the system")
    void getAllProfessionals_withNoRecords_shouldReturnEmptyList() {
        Mockito.when(professionalRepository.findAll()).thenReturn(List.of());

        List<ProfessionalResponse> responses = professionalService.getAllProfessionals();

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Returns professional profile data when valid database ID is provided")
    void getProfessionalById_withValidId_shouldReturnProfile() {
        Mockito.when(professionalRepository.findById(PROFILE_ID)).thenReturn(Optional.of(testProfile));

        ProfessionalResponse response = professionalService.getProfessionalById(PROFILE_ID);

        assertThat(response.id()).isEqualTo(PROFILE_ID);
        assertThat(response.userId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("Throws NotFoundException when searching professional by a non-existent database ID")
    void getProfessionalById_withInvalidId_shouldThrowNotFoundException() {
        Mockito.when(professionalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.getProfessionalById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Professional profile not found");
    }

    @Test
    @DisplayName("Throws ValidationException when repository save fails during creation")
    void createProfile_whenSaveFails_shouldThrowValidationException() {
        Mockito.when(professionalRepository.existsByUserId(TEST_USER_ID)).thenReturn(false);
        Mockito.when(professionalRepository.save(any(ProfessionalProfile.class)))
                .thenThrow(new RuntimeException("Data integrity violation"));

        assertThatThrownBy(() -> professionalService.createProfile(TEST_USER_ID, testRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Error creating professional profile: Invalid data provided");
    }

    @Test
    @DisplayName("Throws ValidationException when repository save fails during update")
    void updateMyProfile_whenSaveFails_shouldThrowValidationException() {
        ProfessionalUpdateRequest update = new ProfessionalUpdateRequest(
                "+56999999999", null, null, null, null, null, null, null, null, null, null
        );

        Mockito.when(professionalRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));
        Mockito.when(professionalRepository.save(any(ProfessionalProfile.class)))
                .thenThrow(new RuntimeException("Database constraint failure"));

        assertThatThrownBy(() -> professionalService.updateMyProfile(TEST_USER_ID, update))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Error updating professional profile: Invalid data provided");
    }
}