package com.owner.pyg_owner.services;

import com.owner.pyg_owner.clients.AuthServiceClient;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.dto.responses.TokenValidationResponse;
import com.owner.pyg_owner.models.OwnerProfile;
import com.owner.pyg_owner.repositories.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private OwnerService ownerService;

    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.test.signature";
    private static final Long USER_ID = 1L;

    private TokenValidationResponse validToken;
    private OwnerCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        validToken = TokenValidationResponse.builder()
                .userId(USER_ID)
                .username("ana.gonzalez")
                .role("ROLE_OWNER")
                .valid(true)
                .expiresAt(System.currentTimeMillis() + 3600000L)
                .build();

        testRequest = new OwnerCreateRequest(
                "+56912345678",
                "Av. Siempreviva 742, Santiago",
                LocalDate.of(1990, 5, 15)
        );
    }


    @Test
    @DisplayName("Creates new profile when user has none")
    void createOrUpdateProfile_withNoExistingProfile_shouldCreateOne() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(ownerRepo.save(any(OwnerProfile.class)))
                .thenAnswer(invocation -> {
                    OwnerProfile p = invocation.getArgument(0);
                    p.setId(10L);
                    return p;
                });

        OwnerResponse response = ownerService.createOrUpdateProfile(BEARER_TOKEN, testRequest);

        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.phone()).isEqualTo("+56912345678");
        assertThat(response.address()).isEqualTo("Av. Siempreviva 742, Santiago");
        assertThat(response.birthDate()).isEqualTo(LocalDate.of(1990, 5, 15));
        verify(ownerRepo, times(1)).save(any(OwnerProfile.class));
    }

    @Test
    @DisplayName("Updates existing profile without creating a new one")
    void createOrUpdateProfile_withExistingProfile_shouldUpdateIt() {
        OwnerProfile existingProfile = OwnerProfile.builder()
                .id(10L)
                .userId(USER_ID)
                .phone("+56900000000")
                .address("Old address")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(existingProfile));
        when(ownerRepo.save(any(OwnerProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OwnerCreateRequest updateRequest = new OwnerCreateRequest(
                "+56999999999",
                "New address",
                LocalDate.of(1990, 5, 15)
        );

        OwnerResponse response = ownerService.createOrUpdateProfile(BEARER_TOKEN, updateRequest);

        assertThat(response.phone()).isEqualTo("+56999999999");
        assertThat(response.address()).isEqualTo("New address");
        assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Passes the full Bearer token to the auth client unmodified")
    void createOrUpdateProfile_shouldForwardFullTokenToAuthClient() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(ownerRepo.save(any(OwnerProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ownerService.createOrUpdateProfile(BEARER_TOKEN, testRequest);

        verify(authServiceClient, times(1)).validateToken(BEARER_TOKEN);
    }

    @Test
    @DisplayName("Invalid token causes exception before repository is touched")
    void createOrUpdateProfile_withInvalidToken_shouldThrowWithoutTouchingRepo() {
        when(authServiceClient.validateToken(BEARER_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        assertThatThrownBy(() ->
                ownerService.createOrUpdateProfile(BEARER_TOKEN, testRequest))
                .isInstanceOf(RuntimeException.class);

        verify(ownerRepo, never()).findByUserId(any());
        verify(ownerRepo, never()).save(any());
    }


    @Test
    @DisplayName("Returns profile correctly when it exists")
    void getMyProfile_withExistingProfile_shouldReturnIt() {
        OwnerProfile profile = OwnerProfile.builder()
                .id(10L)
                .userId(USER_ID)
                .phone("+56912345678")
                .address("Av. Siempreviva 742")
                .birthDate(LocalDate.of(1990, 5, 15))
                .build();

        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(profile));

        OwnerResponse response = ownerService.getMyProfile(BEARER_TOKEN);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.phone()).isEqualTo("+56912345678");
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when profile does not exist")
    void getMyProfile_withNoProfile_shouldThrowEntityNotFoundException() {
        when(authServiceClient.validateToken(BEARER_TOKEN)).thenReturn(validToken);
        when(ownerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.getMyProfile(BEARER_TOKEN))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Owner profile not found");
    }

    @Test
    @DisplayName("Auth failure in getMyProfile prevents repository access")
    void getMyProfile_withInvalidToken_shouldNotQueryRepository() {
        when(authServiceClient.validateToken(BEARER_TOKEN))
                .thenThrow(new RuntimeException("Auth service unavailable"));

        assertThatThrownBy(() -> ownerService.getMyProfile(BEARER_TOKEN))
                .isInstanceOf(RuntimeException.class);

        verify(ownerRepo, never()).findByUserId(any());
    }
}