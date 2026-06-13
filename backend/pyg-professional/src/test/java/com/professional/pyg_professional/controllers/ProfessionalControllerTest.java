package com.professional.pyg_professional.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.services.ProfessionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = ProfessionalController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfessionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfessionalService professionalService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private static final Long TEST_USER_ID = 123L;
    private static final Long PROFILE_ID = 10L;

    private ProfessionalResponse testResponse;
    private ProfessionalRequest testRequest;

    @BeforeEach
    void setUp() {
        testResponse = new ProfessionalResponse(
                PROFILE_ID, TEST_USER_ID, "+56912345678", "Linares, Chile",
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
        Mockito.when(professionalService.createProfile(eq(TEST_USER_ID), any(ProfessionalRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/professionals/profile")
                        .header("X-User-Id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PROFILE_ID))
                .andExpect(jsonPath("$.profession").value("Feline ethologist"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Create profile returns 400 when required fields are blank")
    void createProfile_withInvalidBody_shouldReturn400() throws Exception {
        ProfessionalRequest invalidRequest = new ProfessionalRequest(
                "",
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
                        .header("X-User-Id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        Mockito.verify(professionalService, Mockito.never()).createProfile(any(), any());
    }

    @Test
    @DisplayName("Create profile returns 409 when profile already exists")
    void createProfile_whenProfileAlreadyExists_shouldReturn409() throws Exception {
        Mockito.when(professionalService.createProfile(any(Long.class), any(ProfessionalRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Professional profile already exists"));

        mockMvc.perform(post("/professionals/profile")
                        .header("X-User-Id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Get my profile returns 200 with professional data")
    void getMyProfile_withValidUid_shouldReturn200() throws Exception {
        Mockito.when(professionalService.getMyProfile(TEST_USER_ID)).thenReturn(testResponse);

        mockMvc.perform(get("/professionals/profile")
                        .header("X-User-Id", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROFILE_ID))
                .andExpect(jsonPath("$.profession").value("Feline ethologist"));
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
                PROFILE_ID, TEST_USER_ID, "+56999999999", "Linares, Chile",
                LocalDate.of(1998, 5, 10), "Feline ethologist",
                "Updated bio", 4, "CAT", 0.0, 0,
                null, null, null, "Mon-Fri 10:00-18:00", "ACTIVE",
                LocalDate.now(), LocalDate.now()
        );

        Mockito.when(professionalService.updateMyProfile(eq(TEST_USER_ID), any(ProfessionalUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/professionals/profile")
                        .header("X-User-Id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+56999999999"))
                .andExpect(jsonPath("$.bio").value("Updated bio"));
    }

    @Test
    @DisplayName("Get all professionals returns 200 with list")
    void getAllProfessionals_shouldReturn200() throws Exception {
        Mockito.when(professionalService.getAllProfessionals())
                .thenReturn(List.of(testResponse));

        mockMvc.perform(get("/professionals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].profession").value("Feline ethologist"));
    }

    @Test
    @DisplayName("Get all professionals returns empty list when none exist")
    void getAllProfessionals_withNoProfessionals_shouldReturnEmptyList() throws Exception {
        Mockito.when(professionalService.getAllProfessionals()).thenReturn(List.of());

        mockMvc.perform(get("/professionals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Get professional by ID returns 200 with correct data")
    void getProfessionalById_withValidId_shouldReturn200() throws Exception {
        Mockito.when(professionalService.getProfessionalById(PROFILE_ID))
                .thenReturn(testResponse);

        mockMvc.perform(get("/professionals/{id}", PROFILE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROFILE_ID));
    }
}