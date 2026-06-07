package com.owner.pyg_owner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.services.PetService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.servlet.ServletException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PetControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private static final String BEARER_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiJ9.test.signature";

    private PetResponse petResponse;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(petController)
                .build();

        objectMapper = new ObjectMapper();

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
    @DisplayName("Add pet returns 200 with pet response")
    void addPet_withValidRequest_shouldReturn200() throws Exception {

        PetRequest request = new PetRequest(
                "Milo",
                "DOG",
                "Labrador",
                4,
                true,
                "MALE",
                "Friendly and energetic"
        );

        when(petService.addPet(eq(BEARER_TOKEN), any(PetRequest.class)))
                .thenReturn(petResponse);

        mockMvc.perform(post("/pets")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milo"))
                .andExpect(jsonPath("$.type").value("DOG"));
    }

    @Test
    @DisplayName("Get pets by owner returns 200")
    void getPetsByOwner_shouldReturn200() throws Exception {

        when(petService.getPetsByOwner(BEARER_TOKEN))
                .thenReturn(List.of(petResponse));

        mockMvc.perform(get("/pets")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Milo"));
    }

    @Test
    @DisplayName("Get pet by id returns 200")
    void getPetById_shouldReturn200() throws Exception {

        when(petService.getPetById(BEARER_TOKEN, 1L))
                .thenReturn(petResponse);

        mockMvc.perform(get("/pets/1")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milo"));
    }

    @Test
    @DisplayName("Get pet by id returns 500 when pet not found")
    void getPetById_whenPetNotFound_shouldReturn500() throws Exception {
        when(petService.getPetById(any(), any()))
                .thenThrow(new EntityNotFoundException("Pet not found"));

        ServletException exception = assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/pets/999")
                    .header("Authorization", BEARER_TOKEN));
        });
        assertTrue(exception.getCause() instanceof EntityNotFoundException);
        assertTrue(exception.getCause().getMessage().contains("Pet not found"));
    }
}