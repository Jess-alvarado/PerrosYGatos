package com.professional.pyg_professional.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.security.JwtAuthenticationFilter;
import com.professional.pyg_professional.services.ProfessionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ProfessionalController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=dGVzdFNlY3JldEtleVBhcmFQcnVlYmFzUHlnQXV0aDEyMzQ1Njc4OTA=",
        "jwt.expiration=3600000",
        "jwt.refresh-token=604800000"
})
class ProfessionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfessionalService professionalService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.test.signature";
    private static final Long PROFILE_ID = 10L;

    private ProfessionalResponse testResponse;
    private ProfessionalRequest testRequest;

    @BeforeEach
    void setUp() {
        testResponse = new ProfessionalResponse(
                PROFILE_ID, 1L, "+56912345678", "Linares, Chile",
                LocalDate.of(1998, 5, 10), "Feline ethologist",
                "Specialist in feline behavior", 4, "CAT",
                0.0, 0, null, null, null,
                "Mon-Fri 10:00-18:00", "ACTIVE",
                LocalDate.now(), LocalDate.now()
        );

        testRequest = new ProfessionalRequest(
                "+56912345678", "Linares, Chile",
                LocalDate.of(1998, 5, 10), "Feline ethologist",
                "Specialist in feline behavior", 4, "CAT",
                null, null, null, "Mon-Fri 10:00-18:00"
        );
    }

    @Test
    @DisplayName("Create profile returns 201 with professional response")
    void createProfile_withValidRequest_shouldReturn201() throws Exception {
        when(professionalService.createProfile(eq(BEARER_TOKEN), any(ProfessionalRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/professionals/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PROFILE_ID))
                .andExpect(jsonPath("$.profession").value("Feline ethologist"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Create profile passes Authorization header intact to service")
    void createProfile_shouldForwardAuthorizationHeaderToService() throws Exception {
        when(professionalService.createProfile(any(), any())).thenReturn(testResponse);

        mockMvc.perform(post("/professionals/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());

        verify(professionalService, times(1))
                .createProfile(eq(BEARER_TOKEN), any(ProfessionalRequest.class));
    }

    @Test
    @DisplayName("Create profile returns 400 when required fields are blank")
    void createProfile_withInvalidBody_shouldReturn400() throws Exception {
        ProfessionalRequest invalidRequest = new ProfessionalRequest(
                "",                          // phone @NotBlank — inválido
                "Linares, Chile",
                LocalDate.of(1998, 5, 10),
                "Feline ethologist",
                "Specialist in feline behavior",
                4,
                "CAT",
                null, null, null,
                "Mon-Fri 10:00-18:00"
        );

        mockMvc.perform(post("/professionals/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(professionalService, never()).createProfile(any(), any());
    }

    @Test
    @DisplayName("Create profile returns 409 when profile already exists")
    void createProfile_whenProfileAlreadyExists_shouldReturn409() throws Exception {
        when(professionalService.createProfile(any(), any()))
                .thenThrow(new ResponseStatusException(CONFLICT, "Professional profile already exists"));

        mockMvc.perform(post("/professionals/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Get my profile returns 200 with professional data")
    void getMyProfile_withValidToken_shouldReturn200() throws Exception {
        when(professionalService.getMyProfile(BEARER_TOKEN)).thenReturn(testResponse);

        mockMvc.perform(get("/professionals/profile")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROFILE_ID))
                .andExpect(jsonPath("$.profession").value("Feline ethologist"));
    }

    @Test
    @DisplayName("Get my profile passes Authorization header intact to service")
    void getMyProfile_shouldForwardAuthorizationHeaderToService() throws Exception {
        when(professionalService.getMyProfile(any())).thenReturn(testResponse);

        mockMvc.perform(get("/professionals/profile")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk());

        verify(professionalService, times(1)).getMyProfile(eq(BEARER_TOKEN));
    }


    @Test
    @DisplayName("Update profile returns 200 with updated data")
    void updateMyProfile_withValidRequest_shouldReturn200() throws Exception {
        ProfessionalUpdateRequest updateRequest = new ProfessionalUpdateRequest(
                "+56999999999", null, null, null,
                "Updated bio", null, null, null,
                null, null, null
        );

        ProfessionalResponse updatedResponse = new ProfessionalResponse(
                PROFILE_ID, 1L, "+56999999999", "Linares, Chile",
                LocalDate.of(1998, 5, 10), "Feline ethologist",
                "Updated bio", 4, "CAT", 0.0, 0,
                null, null, null, "Mon-Fri 10:00-18:00", "ACTIVE",
                LocalDate.now(), LocalDate.now()
        );

        when(professionalService.updateMyProfile(eq(BEARER_TOKEN), any(ProfessionalUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/professionals/profile")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+56999999999"))
                .andExpect(jsonPath("$.bio").value("Updated bio"));
    }


    @Test
    @DisplayName("Get all professionals returns 200 with list")
    void getAllProfessionals_withValidToken_shouldReturn200() throws Exception {
        when(professionalService.getAllProfessionals(BEARER_TOKEN))
                .thenReturn(List.of(testResponse));

        mockMvc.perform(get("/professionals")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].profession").value("Feline ethologist"));
    }

    @Test
    @DisplayName("Get all professionals returns empty list when none exist")
    void getAllProfessionals_withNoProfessionals_shouldReturnEmptyList() throws Exception {
        when(professionalService.getAllProfessionals(any())).thenReturn(List.of());

        mockMvc.perform(get("/professionals")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @DisplayName("Get professional by ID returns 200 with correct data")
    void getProfessionalById_withValidId_shouldReturn200() throws Exception {
        when(professionalService.getProfessionalById(BEARER_TOKEN, PROFILE_ID))
                .thenReturn(testResponse);

        mockMvc.perform(get("/professionals/{id}", PROFILE_ID)
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROFILE_ID));
    }

    @Test
    @DisplayName("Get professional by ID passes correct ID and token to service")
    void getProfessionalById_shouldForwardIdAndTokenToService() throws Exception {
        when(professionalService.getProfessionalById(any(), any())).thenReturn(testResponse);

        mockMvc.perform(get("/professionals/{id}", PROFILE_ID)
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk());

        verify(professionalService, times(1))
                .getProfessionalById(eq(BEARER_TOKEN), eq(PROFILE_ID));
    }
}