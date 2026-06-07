package com.owner.pyg_owner.services;

import com.owner.pyg_owner.clients.AuthServiceClient;
import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.dto.responses.TokenValidationResponse;
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

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private PetService petService;

    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.test.signature";
    private static final Long USER_ID = 1L;
    private static final Long OWNER_PROFILE_ID = 10L;
    private static final Long PET_ID = 100L;

    private TokenValidationResponse validToken;
    private OwnerProfile ownerProfile;

    @BeforeEach
    void setUp() {
        validToken = TokenValidationResponse.builder()
                .userId(USER_ID)
                .username("ana.gonzalez")
                .role("ROLE_OWNER")
                .valid(true)
                .expiresAt(System.currentTimeMillis() + 3600000L)
                .build();

        ownerProfile = OwnerProfile.builder()
                .id(OWNER_PROFILE_ID)
                .userId(USER_ID)
                .phone("+56912345678")
                .address("Av. Siempreviva 742")
                .pets(new ArrayList<>())
                .build();
    }


    @Test
    @DisplayName("Adds a dog successfully and returns correct response")
    void addPet_withValidDogRequest_shouldReturnPetResponse() {
        PetRequest request = new PetRequest(
                "Rex", "DOG", "Labrador", 3, true, "Male", "Friendly"
        );

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(ownerProfile));
        when(petRepo.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet p = invocation.getArgument(0);
            p.setId(PET_ID);
            return p;
        });

        PetResponse response = petService.addPet(BEARER_TOKEN, request);

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
    @DisplayName("Accepts lowercase pet type and converts it correctly")
    void addPet_withLowercaseType_shouldNormalizeToEnum() {
        PetRequest request = new PetRequest(
                "Michi", "cat", "Siamese", 2, false, "Female", null
        );

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(ownerProfile));
        when(petRepo.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet p = invocation.getArgument(0);
            p.setId(PET_ID);
            return p;
        });

        PetResponse response = petService.addPet(BEARER_TOKEN, request);

        assertThat(response.type()).isEqualTo("CAT");
    }

    @Test
    @DisplayName("Throws IllegalArgumentException for invalid pet type")
    void addPet_withInvalidType_shouldThrowIllegalArgumentException() {
        // (2) — "BIRD" no existe en el enum PetType, valueOf() lanza esta excepción
        PetRequest request = new PetRequest(
                "Tweety", "BIRD", "Canary", 1, false, "Male", null
        );

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(ownerProfile));

        assertThatThrownBy(() -> petService.addPet(BEARER_TOKEN, request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(petRepo, never()).save(any());
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when owner profile does not exist")
    void addPet_withNoOwnerProfile_shouldThrowEntityNotFoundException() {
        PetRequest request = new PetRequest(
                "Rex", "DOG", "Labrador", 3, true, "Male", null
        );

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.addPet(BEARER_TOKEN, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Owner profile not found");

        verify(petRepo, never()).save(any());
    }

    @Test
    @DisplayName("Invalid token prevents any repository access in addPet")
    void addPet_withInvalidToken_shouldThrowWithoutTouchingRepositories() {
        PetRequest request = new PetRequest(
                "Rex", "DOG", "Labrador", 3, true, "Male", null
        );

        when(authServiceClient.validateToken(BEARER_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        assertThatThrownBy(() -> petService.addPet(BEARER_TOKEN, request))
                .isInstanceOf(RuntimeException.class);

        verify(ownerRepo, never()).findByUserId(any());
        verify(petRepo, never()).save(any());
    }


    @Test
    @DisplayName("Returns all pets for the authenticated owner")
    void getPetsByOwner_withExistingPets_shouldReturnList() {
        // (3) — construimos mascotas directamente en el perfil del owner
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

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(ownerProfile));

        List<PetResponse> responses = petService.getPetsByOwner(BEARER_TOKEN);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Rex");
        assertThat(responses.get(0).type()).isEqualTo("DOG");
        assertThat(responses.get(1).name()).isEqualTo("Michi");
        assertThat(responses.get(1).type()).isEqualTo("CAT");
    }

    @Test
    @DisplayName("Returns empty list when owner has no pets")
    void getPetsByOwner_withNoPets_shouldReturnEmptyList() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(ownerProfile));

        List<PetResponse> responses = petService.getPetsByOwner(BEARER_TOKEN);

        assertThat(responses).isEmpty();
        verify(petRepo, never()).save(any());
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when owner has no profile in getPetsByOwner")
    void getPetsByOwner_withNoOwnerProfile_shouldThrowEntityNotFoundException() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.getPetsByOwner(BEARER_TOKEN))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Owner profile not found");
    }


    @Test
    @DisplayName("Returns specific pet when it belongs to the authenticated owner")
    void getPetById_withValidOwnerAndPet_shouldReturnPetResponse() {
        Pet pet = Pet.builder()
                .id(PET_ID).name("Rex").type(PetType.DOG)
                .breed("Labrador").age(3).sterilized(true)
                .sex("Male").behaviorDescription("Friendly").build();

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(petRepo.findByIdAndOwnerUserId(PET_ID, USER_ID)).thenReturn(Optional.of(pet));

        PetResponse response = petService.getPetById(BEARER_TOKEN, PET_ID);

        assertThat(response.id()).isEqualTo(PET_ID);
        assertThat(response.name()).isEqualTo("Rex");
        assertThat(response.type()).isEqualTo("DOG");
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when pet does not belong to owner")
    void getPetById_withPetFromAnotherOwner_shouldThrowEntityNotFoundException() {
        // (4) — findByIdAndOwnerUserId ya filtra por userId, así que si la mascota
        // es de otro dueño simplemente retorna empty
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(petRepo.findByIdAndOwnerUserId(PET_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.getPetById(BEARER_TOKEN, PET_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pet not found");
    }

    @Test
    @DisplayName("Invalid token prevents pet lookup in getPetById")
    void getPetById_withInvalidToken_shouldThrowWithoutTouchingRepository() {
        when(authServiceClient.validateToken(BEARER_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        assertThatThrownBy(() -> petService.getPetById(BEARER_TOKEN, PET_ID))
                .isInstanceOf(RuntimeException.class);

        verify(petRepo, never()).findByIdAndOwnerUserId(any(), any());
    }
}