package com.owner.pyg_owner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.exceptions.NotFoundException;
import com.owner.pyg_owner.services.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = PetController.class)
@AutoConfigureMockMvc(addFilters = false)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private static final Long TEST_USER_ID = 123L;
    private PetResponse petResponse;

    @BeforeEach
    void setUp() {
        petResponse = new PetResponse(
                1L,
                "Milo",
                "DOG",
                "Labrador",
                4,
                true,
                "MALE",
                "Friendly and energetic"
        );
    }

    @Test
    @DisplayName("Should return 200 OK with pet details when adding a new pet with a valid request")
    void addPet_WithValidRequest_ShouldReturn200() throws Exception {
        PetRequest request = new PetRequest(
                "Milo",
                "DOG",
                "Labrador",
                4,
                true,
                "MALE",
                "Friendly and energetic"
        );

        Mockito.when(petService.addPet(eq(TEST_USER_ID), any(PetRequest.class)))
                .thenReturn(petResponse);

        mockMvc.perform(post("/pets")
                        .header("X-User-Id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milo"))
                .andExpect(jsonPath("$.type").value("DOG"));
    }

    @Test
    @DisplayName("Should return 200 OK with pet list for the authenticated owner")
    void getPetsByOwner_ShouldReturn200() throws Exception {
        Mockito.when(petService.getPetsByOwner(TEST_USER_ID))
                .thenReturn(List.of(petResponse));

        mockMvc.perform(get("/pets")
                        .header("X-User-Id", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Milo"));
    }

    @Test
    @DisplayName("Should return 200 OK with pet details when getting a valid pet by ID")
    void getPetById_ShouldReturn200() throws Exception {
        Mockito.when(petService.getPetById(TEST_USER_ID, 1L))
                .thenReturn(petResponse);

        mockMvc.perform(get("/pets/1")
                        .header("X-User-Id", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milo"));
    }

    @Test
    @DisplayName("Should return 404 Not Found status when pet is not found in the service layer")
    void getPetById_WhenPetNotFound_ShouldReturn404NotFound() throws Exception {
        Mockito.when(petService.getPetById(any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException("Pet not found"));

        mockMvc.perform(get("/pets/999")
                        .header("X-User-Id", TEST_USER_ID))
                .andExpect(status().isNotFound());
    }
}