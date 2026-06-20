package com.owner.pyg_owner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.requests.OwnerUpdateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = OwnerController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "LOG_LEVEL=ERROR",
        "logging.level.root=ERROR"
})
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OwnerService ownerService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private OwnerResponse mockResponse;
    private OwnerCreateRequest mockCreateRequest;
    private final Long testUserId = 123L;

    @BeforeEach
    void setUp() {
        mockCreateRequest = new OwnerCreateRequest(
                "+56912345678",
                "Calle Falsa 123, Linares",
                LocalDate.of(1995, 5, 15)
        );

        mockResponse = new OwnerResponse(
                1L,
                testUserId,
                "+56912345678",
                "Calle Falsa 123, Linares",
                LocalDate.of(1995, 5, 15)
        );
    }

    @Test
    @DisplayName("Should successfully create profile and return 201 Created when valid data is provided")
    void createProfile_ShouldReturn201Created() throws Exception {
        Mockito.when(ownerService.createProfile(eq(testUserId), any(OwnerCreateRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/owners/profile")
                        .header("X-User-Id", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.phone").value("+56912345678"))
                .andExpect(jsonPath("$.address").value("Calle Falsa 123, Linares"));
    }

    @Test
    @DisplayName("Should successfully update profile and return 200 Ok when valid partial data is provided")
    void updateProfile_ShouldReturn200Ok() throws Exception {
        OwnerUpdateRequest updateRequest = new OwnerUpdateRequest(
                "+56987654321",
                "Nueva Direccion 456, Linares",
                null
        );

        OwnerResponse updatedResponse = new OwnerResponse(
                1L, testUserId, "+56987654321", "Nueva Direccion 456, Linares", LocalDate.of(1995, 5, 15)
        );

        Mockito.when(ownerService.updateProfile(eq(testUserId), any(OwnerUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/owners/profile")
                        .header("X-User-Id", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+56987654321"))
                .andExpect(jsonPath("$.address").value("Nueva Direccion 456, Linares"));
    }

    @Test
    @DisplayName("Should return authenticated owner profile details when valid X-User-Id header is present")
    void getMyProfile_ShouldReturn200Ok() throws Exception {
        Mockito.when(ownerService.getMyProfile(testUserId))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/owners/profile")
                        .header("X-User-Id", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.phone").value("+56912345678"));
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when X-User-Id header is missing from the request")
    void getMyProfile_WhenHeaderIsMissing_ShouldReturn500InternalServerError() throws Exception {
        mockMvc.perform(get("/owners/profile"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_ERROR"));
    }
}