package com.owner.pyg_owner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.services.OwnerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.owner.pyg_owner.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.owner.pyg_owner.services.PetService;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;


@WebMvcTest(controllers = OwnerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.test.signature";

    private OwnerResponse testOwnerResponse;

    @BeforeEach
    void setUp() {
        testOwnerResponse = new OwnerResponse(
                10L,
                1L,
                "+56912345678",
                "Av. Siempreviva 742, Santiago",
                LocalDate.of(1990, 5, 15)
        );
    }


    @Test
    @DisplayName("Upsert profile returns 200 with owner response")
    void upsertProfile_withValidRequest_shouldReturn200() throws Exception {
        OwnerCreateRequest request = new OwnerCreateRequest(
                "+56912345678",
                "Av. Siempreviva 742, Santiago",
                LocalDate.of(1990, 5, 15)
        );

        when(ownerService.createOrUpdateProfile(eq(BEARER_TOKEN), any(OwnerCreateRequest.class)))
                .thenReturn(testOwnerResponse);

        mockMvc.perform(post("/owners/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.phone").value("+56912345678"))
                .andExpect(jsonPath("$.address").value("Av. Siempreviva 742, Santiago"));
    }

    @Test
    @DisplayName("Upsert profile passes Authorization header intact to service")
    void upsertProfile_shouldForwardAuthorizationHeaderToService() throws Exception {
        OwnerCreateRequest request = new OwnerCreateRequest(
                "+56912345678",
                "Av. Siempreviva 742, Santiago",
                LocalDate.of(1990, 5, 15)
        );

        when(ownerService.createOrUpdateProfile(any(), any())).thenReturn(testOwnerResponse);

        mockMvc.perform(post("/owners/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(ownerService, times(1))
                .createOrUpdateProfile(eq(BEARER_TOKEN), any(OwnerCreateRequest.class));
    }

    @Test
    @DisplayName("Upsert profile returns 400 when request body is invalid")
    void upsertProfile_withInvalidBody_shouldReturn400() throws Exception {
        OwnerCreateRequest invalidRequest = new OwnerCreateRequest(
                "",
                "",
                LocalDate.of(1990, 5, 15)
        );

        mockMvc.perform(post("/owners/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ownerService, never()).createOrUpdateProfile(any(), any());
    }

    @Test
    @DisplayName("Upsert profile throws RuntimeException when service fails")
    void upsertProfile_whenServiceFails_shouldThrowException() throws Exception {

        OwnerCreateRequest request = new OwnerCreateRequest(
                "+56912345678",
                "Av. Siempreviva 742",
                LocalDate.of(1990, 5, 15)
        );

        when(ownerService.createOrUpdateProfile(any(), any()))
                .thenThrow(new RuntimeException("Auth service unavailable"));

        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class,
                () -> {
                    mockMvc.perform(post("/owners/profile")
                            .header("Authorization", BEARER_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)));
                }
        );

        assertNotNull(exception.getCause(), "The root exception should not be null");
        assertTrue(exception.getCause() instanceof RuntimeException, "Should be a RuntimeException");
        assertEquals("Auth service unavailable", exception.getCause().getMessage());
    }


    @Test
    @DisplayName("Get profile returns 200 with owner data")
    void getMyProfile_withValidToken_shouldReturn200() throws Exception {
        when(ownerService.getMyProfile(BEARER_TOKEN)).thenReturn(testOwnerResponse);

        mockMvc.perform(get("/owners/profile")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.phone").value("+56912345678"));
    }

    @Test
    @DisplayName("Get profile passes Authorization header intact to service")
    void getMyProfile_shouldForwardAuthorizationHeaderToService() throws Exception {
        when(ownerService.getMyProfile(any())).thenReturn(testOwnerResponse);

        mockMvc.perform(get("/owners/profile")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk());

        verify(ownerService, times(1)).getMyProfile(eq(BEARER_TOKEN));
    }

    @Test
    @DisplayName("Get profile throws EntityNotFoundException when profile does not exist")
    void getMyProfile_whenProfileNotFound_shouldThrowException() throws Exception {

        when(ownerService.getMyProfile(any()))
                .thenThrow(new EntityNotFoundException("Owner profile not found"));

        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class,
                () -> {
                    mockMvc.perform(get("/owners/profile")
                            .header("Authorization", BEARER_TOKEN));
                }
        );

        assertNotNull(exception.getCause(), "The root exception should not be null");
        assertTrue(exception.getCause() instanceof EntityNotFoundException, "Should be an EntityNotFoundException");
        assertEquals("Owner profile not found", exception.getCause().getMessage());
    }
}