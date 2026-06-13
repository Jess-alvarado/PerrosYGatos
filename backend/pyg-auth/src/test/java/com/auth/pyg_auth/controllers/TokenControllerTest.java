package com.auth.pyg_auth.controllers;

import com.auth.pyg_auth.services.AccessTokenBlacklistService;
import com.auth.pyg_auth.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @Test
    @DisplayName("Should return valid true and correct data when token is valid and active")
    void validateToken_withValidToken_shouldReturnTokenStatusResponse() throws Exception {
        String validToken = "valid.mocked.jwt.token";

        Claims mockClaims = new DefaultClaims();
        mockClaims.setSubject("jess@perrosygatos.cl");
        mockClaims.put("uid", 1L);
        mockClaims.put("role", "ROLE_OWNER");
        mockClaims.setExpiration(new Date(System.currentTimeMillis() + 3600000L));

        when(jwtService.getAllClaims(anyString())).thenReturn(mockClaims);

        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("jess@perrosygatos.cl"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("ROLE_OWNER"));
    }

    @Test
    @DisplayName("Should return valid false when JwtService throws an exception")
    void validateToken_withExpiredOrInvalidToken_shouldReturnValidFalse() throws Exception {
        String invalidToken = "invalid.or.expired.token";

        when(jwtService.getAllClaims(anyString()))
                .thenThrow(new RuntimeException("JWT Expired"));

        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Should return valid false when Authorization header is missing or malformed")
    void validateToken_withMissingHeader_shouldReturnValidFalse() throws Exception {
        mockMvc.perform(post("/api/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }
}