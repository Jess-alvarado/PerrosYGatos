package com.owner.pyg_owner.services;

import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.models.OwnerProfile;
import com.owner.pyg_owner.models.Pet;
import com.owner.pyg_owner.models.PetType;
import com.owner.pyg_owner.repositories.OwnerRepository;
import com.owner.pyg_owner.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepo;

    @Mock
    private OwnerRepository ownerRepo;

    @InjectMocks
    private PetService petService;

    private static final Long TEST_USER_ID = 123L;
    private static final Long OWNER_PROFILE_ID = 10L;
    private static final Long PET_ID = 100L;

    private OwnerProfile ownerProfile;

    @BeforeEach
    void setUp() {
        ownerProfile = OwnerProfile.builder()
                .id(OWNER_PROFILE_ID)
                .userId(TEST_USER_ID)
                .phone("+56912345678")
                .address("Av. Siempreviva 742, Linares")
                .pets(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should successfully add a new pet and return its response details")
    void addPet_WithValidDogRequest_ShouldReturnPetResponse() {
        PetRequest request = new PetRequest(
                "Rex", "DOG", "Labrador", 3, true, "Male", "Friendly"
        );

        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(ownerProfile));
        when(petRepo.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet p = invocation.getArgument(0);
            p.setId(PET_ID);
            return p;
        });

        PetResponse response = petService.addPet(TEST_USER_ID, request);

        assertThat(response.id()).isEqualTo(PET_ID);
        assertThat(response.name()).isEqualTo("Rex");
        assertThat(response.type()).isEqualTo("DOG");
        assertThat(response.breed()).isEqualTo("Labrador");
        assertThat(response.age()).isEqualTo(3);
        assertThat(response.sterilized()).isTrue();
        assertThat(response.sex()).isEqualTo("Male");
        assertThat(response.behaviorDescription()).isEqualTo("Friendly");
        verify(petRepo, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Should normalize lowercase pet type input to correct uppercase enum string")
    void addPet_WithLowercaseType_ShouldNormalizeToEnum() {
        // Given
        PetRequest request = new PetRequest(
                "Michi", "cat", "Siamese", 2, false, "Female", null
        );

        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(ownerProfile));
        when(petRepo.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet p = invocation.getArgument(0);
            p.setId(PET_ID);
            return p;
        });

        PetResponse response = petService.addPet(TEST_USER_ID, request);

        assertThat(response.type()).isEqualTo("CAT");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when pet type does not exist in the system")
    void addPet_WithInvalidType_ShouldThrowIllegalArgumentException() {
        PetRequest request = new PetRequest(
                "Tweety", "BIRD", "Canary", 1, false, "Male", null
        );

        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(ownerProfile));

        assertThatThrownBy(() -> petService.addPet(TEST_USER_ID, request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(petRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when attempting to add a pet to a non-existent owner profile")
    void addPet_WithNoOwnerProfile_ShouldThrowEntityNotFoundException() {
        PetRequest request = new PetRequest(
                "Rex", "DOG", "Labrador", 3, true, "Male", null
        );

        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.addPet(TEST_USER_ID, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Owner profile not found");

        verify(petRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should return a list of pets linked to the authenticated user's owner profile")
    void getPetsByOwner_WithExistingPets_ShouldReturnList() {
        Pet dog = Pet.builder()
                .id(101L).name("Rex").type(PetType.DOG)
                .breed("Labrador").age(3).sterilized(true)
                .sex("Male").behaviorDescription("Friendly").build();

        Pet cat = Pet.builder()
                .id(102L).name("Michi").type(PetType.CAT)
                .breed("Siamese").age(2).sterilized(false)
                .sex("Female").behaviorDescription(null).build();

        ownerProfile.getPets().add(dog);
        ownerProfile.getPets().add(cat);

        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(ownerProfile));

        List<PetResponse> responses = petService.getPetsByOwner(TEST_USER_ID);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Rex");
        assertThat(responses.get(0).type()).isEqualTo("DOG");
        assertThat(responses.get(1).name()).isEqualTo("Michi");
        assertThat(responses.get(1).type()).isEqualTo("CAT");
    }

    @Test
    @DisplayName("Should return an empty list when owner has no registered pets")
    void getPetsByOwner_WithNoPets_ShouldReturnEmptyList() {
        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(ownerProfile));

        List<PetResponse> responses = petService.getPetsByOwner(TEST_USER_ID);

        assertThat(responses).isEmpty();
        verifyNoInteractions(petRepo);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when querying pets for a user with no profile record")
    void getPetsByOwner_WithNoOwnerProfile_ShouldThrowEntityNotFoundException() {
        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.getPetsByOwner(TEST_USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Owner profile not found");
    }

    @Test
    @DisplayName("Should return pet data when pet exists and belongs to the owner")
    void getPetById_WithValidOwnerAndPet_ShouldReturnPetResponse() {
        Pet pet = Pet.builder()
                .id(PET_ID).name("Rex").type(PetType.DOG)
                .breed("Labrador").age(3).sterilized(true)
                .sex("Male").behaviorDescription("Friendly").build();

        when(petRepo.findByIdAndOwnerUserId(PET_ID, TEST_USER_ID)).thenReturn(Optional.of(pet));

        PetResponse response = petService.getPetById(TEST_USER_ID, PET_ID);

        assertThat(response.id()).isEqualTo(PET_ID);
        assertThat(response.name()).isEqualTo("Rex");
        assertThat(response.type()).isEqualTo("DOG");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when pet id is missing or does not match owner user id")
    void getPetById_WithPetFromAnotherOwner_ShouldThrowEntityNotFoundException() {
        when(petRepo.findByIdAndOwnerUserId(PET_ID, TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.getPetById(TEST_USER_ID, PET_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pet not found");
    }
}