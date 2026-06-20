package com.owner.pyg_owner.services;

import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.requests.OwnerUpdateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.exceptions.AlreadyExistsException;
import com.owner.pyg_owner.exceptions.NotFoundException;
import com.owner.pyg_owner.exceptions.ValidationException;
import com.owner.pyg_owner.models.OwnerProfile;
import com.owner.pyg_owner.repositories.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepo;

    @InjectMocks
    private OwnerService ownerService;

    private static final Long TEST_USER_ID = 123L;
    private OwnerCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new OwnerCreateRequest(
                "+56912345678",
                "Av. Siempreviva 742, Linares",
                LocalDate.of(1990, 5, 15)
        );
    }

    @Test
    @DisplayName("Should create a new owner profile when user has no existing profile")
    void createProfile_WithNoExistingProfile_ShouldCreateNewProfile() {
        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        when(ownerRepo.save(any(OwnerProfile.class)))
                .thenAnswer(invocation -> {
                    OwnerProfile p = invocation.getArgument(0);
                    p.setId(10L);
                    return p;
                });

        OwnerResponse response = ownerService.createProfile(TEST_USER_ID, testRequest);

        assertThat(response.userId()).isEqualTo(TEST_USER_ID);
        assertThat(response.phone()).isEqualTo("+56912345678");
        assertThat(response.address()).isEqualTo("Av. Siempreviva 742, Linares");
        assertThat(response.birthDate()).isEqualTo(LocalDate.of(1990, 5, 15));
        verify(ownerRepo, times(1)).save(any(OwnerProfile.class));
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when trying to create a profile that already exists")
    void createProfile_WithExistingProfile_ShouldThrowAlreadyExistsException() {
        OwnerProfile existingProfile = OwnerProfile.builder().id(10L).userId(TEST_USER_ID).build();
        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(existingProfile));

        assertThatThrownBy(() -> ownerService.createProfile(TEST_USER_ID, testRequest))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Owner profile already exists");

        verify(ownerRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should update existing owner profile details successfully")
    void updateProfile_WithExistingProfile_ShouldUpdateCurrentProfile() {
        OwnerProfile existingProfile = OwnerProfile.builder()
                .id(10L)
                .userId(TEST_USER_ID)
                .phone("+56900000000")
                .address("Old address")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(existingProfile));
        when(ownerRepo.save(any(OwnerProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OwnerUpdateRequest updateRequest = new OwnerUpdateRequest(
                "+56999999999",
                "New address",
                LocalDate.of(1990, 5, 15)
        );

        OwnerResponse response = ownerService.updateProfile(TEST_USER_ID, updateRequest);

        assertThat(response.phone()).isEqualTo("+56999999999");
        assertThat(response.address()).isEqualTo("New address");
        assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when trying to update a non-existent profile")
    void updateProfile_WithNoProfileRecord_ShouldThrowNotFoundException() {
        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        OwnerUpdateRequest updateRequest = new OwnerUpdateRequest("+56999999999", "New address", null);

        assertThatThrownBy(() -> ownerService.updateProfile(TEST_USER_ID, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Owner profile not found");
    }

    @Test
    @DisplayName("Should return owner profile data when profile exists for the user id")
    void getMyProfile_WithExistingProfile_ShouldReturnProfileResponse() {
        OwnerProfile profile = OwnerProfile.builder()
                .id(10L)
                .userId(TEST_USER_ID)
                .phone("+56912345678")
                .address("Av. Siempreviva 742, Linares")
                .birthDate(LocalDate.of(1990, 5, 15))
                .build();

        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(profile));

        OwnerResponse response = ownerService.getMyProfile(TEST_USER_ID);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.userId()).isEqualTo(TEST_USER_ID);
        assertThat(response.phone()).isEqualTo("+56912345678");
    }

    @Test
    @DisplayName("Should throw NotFoundException when getting profile for a user id with no profile record")
    void getMyProfile_WithNoProfileRecord_ShouldThrowNotFoundException() {
        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.getMyProfile(TEST_USER_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Owner profile not found");
    }

    @Test
    @DisplayName("Should throw ValidationException when repository save fails during creation")
    void createProfile_WhenSaveFails_ShouldThrowValidationException() {
        when(ownerRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        when(ownerRepo.save(any(OwnerProfile.class))).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> ownerService.createProfile(TEST_USER_ID, testRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Error creating profile: Invalid data provided");
    }
}