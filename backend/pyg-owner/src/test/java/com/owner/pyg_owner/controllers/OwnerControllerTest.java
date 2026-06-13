package com.owner.pyg_owner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
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
    private OwnerCreateRequest mockRequest;
    private final Long testUserId = 123L;

    @BeforeEach
    void setUp() {
        mockRequest = new OwnerCreateRequest(
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
    @DisplayName("Should successfully create or update profile when valid request data and X-User-Id header are provided")
    void upsertProfile_ShouldReturn200Ok() throws Exception {
        Mockito.when(ownerService.createOrUpdateProfile(eq(testUserId), any(OwnerCreateRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/owners/profile")
                        .header("X-User-Id", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.phone").value("+56912345678"))
                .andExpect(jsonPath("$.address").value("Calle Falsa 123, Linares"));
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
    @DisplayName("Should return 400 Bad Request when X-User-Id header is missing from the request")
    void getMyProfile_WhenHeaderIsMissing_ShouldReturn400BadRequest() throws Exception {
        mockMvc.perform(get("/owners/profile"))
                .andExpect(status().isBadRequest());
    }
}